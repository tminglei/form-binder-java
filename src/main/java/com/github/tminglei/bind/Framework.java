package com.github.tminglei.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * framework's core interfaces/implementations
 */
public class Framework {
    @FunctionalInterface
    public interface Messages {
        String get(String key);
    }
    @FunctionalInterface
    public interface PreProcessor {
        Map<String, String> apply(String prefix, Map<String, String> data, Options options);
    }
    @FunctionalInterface
    public interface Constraint {
        List<Map.Entry<String, String>> apply(String name, Map<String, String> data, Messages messages, Options options);
    }
    @FunctionalInterface
    public interface ExtraConstraint<T> {
        List<String> apply(String label, T vObj, Messages messages);
    }
    @FunctionalInterface
    public interface SimpleConstraint {
        String apply(String label, String vString, Messages messages);
    }
    @FunctionalInterface
    public interface TouchedChecker {
        boolean apply(String prefix, Map<String, String> data);
    }
    ///
    public enum InputMode {
        SINGLE, MULTIPLE, POLYMORPHIC
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * A mapping, w/ constraints/processors/options, was used to validate/convert input data
     */
    public interface Mapping<T> {

        /**
         * @return options associated with the mapping
         */
        Options options();

        /**
         * change options associated with the mapping
         * @param setting function used to change the options
         * @return the mapping
         */
        Mapping<T> options(Function<Options, Options> setting);

        /**
         * set label which was used to flag the data node
         * @param label label
         * @return the mapping
         */
        default Mapping<T> label(String label) {
            return options(o -> o._label(label));
        }

        /**
         * attach some pre-processors to the mapping
         * @param newProcessors pre-processors
         * @return the mapping
         */
        default Mapping<T> processor(PreProcessor... newProcessors) {
            return options(o -> o.append_processors(newProcessors));
        }

        /**
         * attach some constraints to the mapping
         * @param newConstraints constraints
         * @return the mapping
         */
        default Mapping<T> constraint(Constraint... newConstraints) {
            return options(o -> o.append_constraints(newConstraints));
        }

        /**
         * attach some extra constraints, which was used to do some extra checking
         * after string was converted to target value, to the mapping
         * @param extraConstraints extra constraints
         * @return the mapping
         */
        Mapping<T> verifying(ExtraConstraint<T>... extraConstraints);

        ///

        /**
         * used to convert raw string value to target type's value
         * @param name full path name
         * @param data data
         * @return converted value
         */
        T convert(String name, Map<String, String> data);

        /**
         * used to validate raw string data values w/ or w/o data keys
         * @param name full path name
         * @param data data
         * @param messages the message holder
         * @param parentOptions parent options
         * @return error list
         */
        List<Map.Entry<String, String>> validate(
                String name, Map<String, String> data, Messages messages, Options parentOptions);

        /**
         * transform this result value to another type
         * @param transform
         * @param <R>
         * @return
         */
        default <R> Mapping<R> mapTo(Function<T, R> transform) {
            return new TransformMapping<>(this, transform);
        }
    }

    /**
     * A wrapper mapping, used to transform converted value to another type
     */
    private static class TransformMapping<T, R> implements Mapping<R> {
        private final Mapping<T> base;
        private final Function<T, R> transform;
        private final List<ExtraConstraint<R>> extraConstraints;

        private final Logger logger = LoggerFactory.getLogger(TransformMapping.class);

        TransformMapping(Mapping<T> base, Function<T, R> transform) {
            this(base, transform, Collections.EMPTY_LIST);
        }
        TransformMapping(Mapping<T> base, Function<T, R> transform, List<ExtraConstraint<R>> extraConstraints) {
            this.base = base;
            this.transform = transform;
            this.extraConstraints = unmodifiableList(extraConstraints);
        }

        @Override
        public Options options() {
            return base.options();
        }

        @Override
        public Mapping<R> options(Function<Options, Options> setting) {
            return new TransformMapping(base.options(setting), transform, extraConstraints);
        }

        @Override
        public Mapping<R> verifying(ExtraConstraint<R>... extraConstraints) {
            return new TransformMapping(base, transform,
                    (List<ExtraConstraint<R>>) appendList(this.extraConstraints, extraConstraints));
        }

        @Override
        public R convert(String name, Map<String, String> data) {
            logger.debug("transforming {}", name);
            return transform.apply(base.convert(name, data));
        }

        @Override
        public List<Map.Entry<String, String>> validate(String name, Map<String, String> data,
                                                        Messages messages, Options parentOptions) {
            List<Map.Entry<String, String>> errors = base.validate(name, data, messages, parentOptions);
            if (errors.isEmpty()) {
                return Optional.ofNullable(convert(name, data))
                        .map(v -> extraValidateRec(name, v, messages, parentOptions, extraConstraints))
                        .orElse(Collections.EMPTY_LIST);
            } else return errors;
        }
    }

    /**
     * In general, a field mapping is an atomic mapping, which doesn't contain other mappings
     */
    public static class FieldMapping<T> implements Mapping<T> {
        private final Options options;
        private final Constraint moreValidate;
        private final List<ExtraConstraint<T>> extraConstraints;
        private final BiFunction<String, Map<String, String>, T> doConvert;

        private final Logger logger = LoggerFactory.getLogger(FieldMapping.class);

        FieldMapping(InputMode inputMode, BiFunction<String, Map<String, String>, T> doConvert) {
            this(inputMode, doConvert, FrameworkUtils.PassValidating, Collections.EMPTY_LIST, Options.EMPTY);
        }
        FieldMapping(InputMode inputMode, BiFunction<String, Map<String, String>, T> doConvert, Constraint moreValidate) {
            this(inputMode, doConvert, moreValidate, Collections.EMPTY_LIST, Options.EMPTY);
        }
        FieldMapping(InputMode inputMode, BiFunction<String, Map<String, String>, T> doConvert,
                     Constraint moreValidate, List<ExtraConstraint<T>> extraConstraints, Options options) {
            this.doConvert = doConvert;
            this.moreValidate = moreValidate;
            this.extraConstraints = unmodifiableList(extraConstraints);
            this.options = options._inputMode(inputMode);
        }

        @Override
        public Options options() {
            return options;
        }

        @Override
        public Mapping<T> options(Function<Options, Options> setting) {
            return new FieldMapping<T>(
                    this.options()._inputMode(),
                    this.doConvert,
                    this.moreValidate,
                    this.extraConstraints,
                    setting.apply(this.options())
                );
        }

        @Override
        public Mapping<T> verifying(ExtraConstraint<T>... extraConstraints) {
            return new FieldMapping<T>(
                    this.options()._inputMode(),
                    this.doConvert,
                    this.moreValidate,
                    appendList(this.extraConstraints, extraConstraints),
                    this.options()
                );
        }

        @Override
        public T convert(String name, Map<String, String> data) {
            logger.debug("converting {}", name);
            Map<String, String> newData = processDataRec(name, data, options(), options()._processors());
            return doConvert.apply(name, newData);
        }

        @Override
        public List<Map.Entry<String, String>> validate(String name, Map<String, String> data,
                                                Messages messages, Options parentOptions) {
            logger.debug("validating {}", name);

            Options theOptions = options().merge(parentOptions);
            Map<String, String> newData = processDataRec(name, data, theOptions, theOptions._processors());

            if (isEmptyInput(name, newData, theOptions._inputMode())
                    && theOptions.ignoreEmpty().orElse(false)
                    && (theOptions.touched() == null || ! theOptions.touched().apply(name, newData))) {
                return Collections.EMPTY_LIST;
            }
            else {
                List<Constraint> validators = appendList(theOptions._ignoreConstraints() ? null : theOptions._constraints(), moreValidate);
                List<Map.Entry<String, String>> errors = validateRec(name, newData, messages, theOptions, validators);
                if (errors.isEmpty()) {
                    return Optional.ofNullable(doConvert.apply(name, newData))
                            .map(v -> extraValidateRec(name, v, messages, theOptions, extraConstraints))
                            .orElse(Collections.EMPTY_LIST);
                } else return errors;
            }
        }
    }

    /**
     * A group mapping is a compound mapping, and is used to construct a complex/nested mapping
     */
    public static class GroupMapping implements Mapping<BindObject> {
        private final Options options;
        private final List<Map.Entry<String, Mapping<?>>> fields;
        private final List<ExtraConstraint<BindObject>> extraConstraints;

        private final Logger logger = LoggerFactory.getLogger(GroupMapping.class);

        GroupMapping(List<Map.Entry<String, Mapping<?>>> fields) {
            this(fields, Collections.EMPTY_LIST, Options.EMPTY);
        }
        GroupMapping(List<Map.Entry<String, Mapping<?>>> fields, List<ExtraConstraint<BindObject>> extraConstraints, Options options) {
            this.fields = unmodifiableList(fields);
            this.extraConstraints = unmodifiableList(extraConstraints);
            this.options = options._inputMode(InputMode.MULTIPLE);
        }

        @Override
        public Options options() {
            return options;
        }

        @Override
        public Mapping<BindObject> options(Function<Options, Options> setting) {
            return new GroupMapping(
                    this.fields,
                    this.extraConstraints,
                    setting.apply(this.options())
                );
        }

        @Override
        public Mapping<BindObject> verifying(ExtraConstraint<BindObject>... extraConstraints) {
            return new GroupMapping(
                    this.fields,
                    appendList(this.extraConstraints, extraConstraints),
                    this.options()
                );
        }

        @Override
        public BindObject convert(String name, Map<String, String> data) {
            logger.debug("converting {}", name);

            Map<String, String> newData = processDataRec(name, data, options, options._processors());
            return isEmptyInput(name, newData, options._inputMode()) ? null
                    : doConvert(name, newData);
        }

        private BindObject doConvert(String name, Map<String, String> data) {
            Map<String, Object> values = new HashMap<>();
            if (!isEmptyInput(name, data, options._inputMode())) {
                for(Map.Entry<String, Mapping<?>> field : fields) {
                    String fullName = isEmptyStr(name) ? field.getKey() : name + "." + field.getKey();
                    Object value = field.getValue().convert(fullName, data);
                    values.put(field.getKey(), value);
                }
            }
            return new BindObject(values);
        }

        @Override
        public List<Map.Entry<String, String>> validate(String name, Map<String, String> data,
                                                Messages messages, Options parentOptions) {
            logger.debug("validating {}", name);

            Options theOptions = options().merge(parentOptions);
            Map<String, String> newData = processDataRec(name, data, theOptions, theOptions._processors());

            if (isEmptyInput(name, newData, theOptions._inputMode())
                    && theOptions.ignoreEmpty().orElse(false)
                    && (theOptions.touched() == null || ! theOptions.touched().apply(name, newData))) {
                return Collections.EMPTY_LIST;
            }
            else {
                List<Constraint> validators = appendList(theOptions._constraints(),
                        (name1, data1, messages1, options1) -> {
                            if (isEmptyInput(name1, data1, options1._inputMode())) return Collections.EMPTY_LIST;
                            else {
                                return fields.stream().flatMap(field -> {
                                    String fullName = isEmptyStr(name1) ? field.getKey() : name1 + "." + field.getKey();
                                    return field.getValue().validate(fullName, data1, messages1, options1)
                                            .stream();
                                }).collect(Collectors.toList());
                            }
                        });

                List<Map.Entry<String, String>> errors = validateRec(name, newData, messages, theOptions, validators);
                if (errors.isEmpty()) {
                    if (isEmptyInput(name, newData, theOptions._inputMode())) return Collections.EMPTY_LIST;
                    else {
                        BindObject vObj = doConvert(name, newData);
                        return extraValidateRec(name, vObj, messages, theOptions, extraConstraints);
                    }
                }
                return errors;
            }
        }
    }

}
