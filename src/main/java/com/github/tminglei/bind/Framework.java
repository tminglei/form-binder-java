package com.github.tminglei.bind;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * framework's core interfaces/implementations
 */
public class Framework {
    @FunctionalInterface
    interface Messages {
        String get(String key);
    }
    @FunctionalInterface
    interface PreProcessor {
        Map<String, String> apply(String prefix, Map<String, String> data, Options options);
    }
    @FunctionalInterface
    interface Constraint {
        List<Map.Entry<String, String>> apply(String name, Map<String, String> data, Messages messages, Options options);
    }
    @FunctionalInterface
    interface ExtraConstraint<T> {
        List<String> apply(String label, T vObject, Messages messages);
    }
    @FunctionalInterface
    interface SimpleConstraint {
        String apply(String label, String vString, Messages messages);
    }
    @FunctionalInterface
    interface TouchedChecker {
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
        Options options();
        Mapping options(Function<Options, Options> setting);

        default Mapping<T> label(String label) {
            return options(o -> o._label(label));
        }
        default Mapping<T> processor(PreProcessor... newProcessors) {
            return options(o -> o.append_processors(newProcessors));
        }
        default Mapping<T> constraint(Constraint... newConstraints) {
            return options(o -> o.append_constraints(newConstraints));
        }
        Mapping<T> verifying(ExtraConstraint<T>... extraConstraints);

        T convert(String name, Map<String, String> data);
        List<Map.Entry<String, String>> validate(
                String name, Map<String, String> data, Messages messages, Options parentOptions);
    }

    /**
     * A wrapper mapping, used to intercept/customize converting or validating of the delegated mapping
     */
    public static class MappingWrapper<T> implements Mapping<T> {
        public final Mapping<T> base;
        public final BiFunction<String, Map<String, String>, T> doConvert;
        public final Constraint doValidate;

        MappingWrapper(Mapping<T> base) {
            this(base, null, null);
        }
        MappingWrapper(Mapping<T> base, BiFunction<String, Map<String, String>, T> doConvert,
                       Constraint doValidate) {
            this.base = base;
            this.doConvert = doConvert;
            this.doValidate = doValidate;
        }

        @Override
        public Options options() {
            return base.options();
        }

        @Override
        public Mapping<T> options(Function<Options, Options> setting) {
            return new MappingWrapper(base.options(setting), doConvert, doValidate);
        }

        @Override
        public Mapping<T> verifying(ExtraConstraint<T>... extraConstraints) {
            return new MappingWrapper(base.verifying(extraConstraints), doConvert, doValidate);
        }

        @Override
        public T convert(String name, Map<String, String> data) {
            if (doConvert == null) return base.convert(name, data);
            else {
                Map<String, String> newData = processDataRec(name, data, options(), options()._processors());
                return doConvert.apply(name, newData);
            }
        }

        @Override
        public List<Map.Entry<String, String>> validate(String name, Map<String, String> data,
                                                Messages messages, Options parentOptions) {
            Options theOptions = options().merge(parentOptions);
            if (doValidate == null) return base.validate(name, data, messages, theOptions);
            else {
                Map<String, String> newData = processDataRec(name, data, theOptions, theOptions._processors());
                return doValidate.apply(name, newData, messages, theOptions);
            }
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
            Map<String, String> newData = processDataRec(name, data, options(), options()._processors());
            return doConvert.apply(name, newData);
        }

        @Override
        public List<Map.Entry<String, String>> validate(String name, Map<String, String> data,
                                                Messages messages, Options parentOptions) {
            Options theOptions = options().merge(parentOptions);
            Map<String, String> newData = processDataRec(name, data, theOptions, theOptions._processors());

            if (theOptions.ignoreEmpty().orElse(false)
                    && (theOptions.touched() == null || !theOptions.touched().apply(name, newData))
                    && isEmptyInput(name, newData, theOptions._inputMode())) {
                return Collections.EMPTY_LIST;
            }
            else {
                List<Constraint> validators = appendList(theOptions._ignoreConstraints() ? null : theOptions._constraints(), moreValidate);
                List<Map.Entry<String, String>> errors = validateRec(name, newData, messages, theOptions, validators);
                if (errors.isEmpty()) {
                    T vObject = doConvert.apply(name, newData);
                    if (vObject != null) {
                        return extraValidateRec(name, vObject, messages, theOptions, extraConstraints);
                    }
                }
                return errors;
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
            Map<String, String> newData = processDataRec(name, data, options, options._processors());
            return isEmptyInput(name, newData, options._inputMode()) ? null
                    : doConvert(name, newData);
        }

        private BindObject doConvert(String name, Map<String, String> data) {
            Map<String, Object> values = new HashMap<>();
            for(Map.Entry<String, Mapping<?>> field : fields) {
                String fullName = isEmptyStr(name) ? field.getKey() : name + "." + field.getKey();
                Object value = field.getValue().convert(fullName, data);
                values.put(fullName, value);
            }
            return new BindObject(values);
        }

        @Override
        public List<Map.Entry<String, String>> validate(String name, Map<String, String> data,
                                                Messages messages, Options parentOptions) {
            Options theOptions = options().merge(parentOptions);
            Map<String, String> newData = processDataRec(name, data, theOptions, theOptions._processors());

            if (theOptions.ignoreEmpty().orElse(false)
                    && (theOptions.touched() == null || !theOptions.touched().apply(name, newData))
                    && isEmptyInput(name, newData, theOptions._inputMode())) {
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
                        BindObject vObject = doConvert(name, newData);
                        if (vObject != null) {
                            return extraValidateRec(name, vObject, messages, theOptions, extraConstraints);
                        }
                    }
                }
                return errors;
            }
        }
    }

}
