package com.github.tminglei.bind;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by tminglei on 6/21/15.
 */
public class framework {
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
        List<ErrMessage> apply(String name, Map<String, String> data, Messages messages, Options options);
    }
    @FunctionalInterface
    interface ExtraConstraint<T> {
        List<String> apply(String label, T vObject, Messages messages);
    }
    @FunctionalInterface
    interface SimpleConstraint {
        String apply(String label, String vString, Messages messages);
    }
    ///
    public enum InputMode {
        SOLO_INPUT, BULK_INPUT, POLY_INPUT;
    }
    ///
    public static class ErrMessage {
        private final String target;
        private final String message;

        ErrMessage(String target, String message) {
            this.target = target;
            this.message = message;
        }
        public String getTarget() {
            return target;
        }
        public String getMessage() {
            return message;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    public interface Mapping<T> {
        Options options();
        Mapping options(Function<Options, Options> setting);

        default Mapping<T> label(String label) {
            return options(o -> Options.setter(o)._label(label).get());
        }
        default Mapping<T> processor(PreProcessor... newProcessors) {
            return options(o -> Options.setter(o).append_processors(newProcessors).get());
        }
        default Mapping<T> constraint(Constraint... newConstraints) {
            return options(o -> Options.setter(o).append_constraints(newConstraints).get());
        }
        default Mapping<T> verifying(ExtraConstraint<T>... extraConstraints) {
            return options(o -> Options.setter(o).append_extraConstraints(extraConstraints).get());
        }

        T convert(String name, Map<String, String> data);
        List<ErrMessage> validate(String name, Map<String, String> data,
                                  Messages messages, Options parentOptions);
    }

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
        public Mapping options(Function<Options, Options> setting) {
            return new MappingWrapper(base.options(setting), doConvert, doValidate);
        }

        @Override
        public T convert(String name, Map<String, String> data) {
            return doConvert != null ? doConvert.apply(name, data) : base.convert(name, data);
        }

        @Override
        public List<ErrMessage> validate(String name, Map<String, String> data,
                                         Messages messages, Options parentOptions) {
            return doValidate != null
                    ? doValidate.apply(name, data, messages, parentOptions)
                    : base.validate(name, data, messages, parentOptions);
        }
    }

    public static class FieldMapping<T> implements Mapping<T> {
        private final Options options;
        private final Constraint moreValidate;
        private final BiFunction<String, Map<String, String>, T> doConvert;

        FieldMapping(InputMode inputMode, BiFunction<String, Map<String, String>, T> doConvert) {
            this(inputMode, doConvert, FrameworkUtils.PassValidating, Options.EMPTY);
        }

        FieldMapping(InputMode inputMode, BiFunction<String, Map<String, String>, T> doConvert,
                     Constraint moreValidate, Options options) {
            this.doConvert = doConvert;
            this.moreValidate = moreValidate;
            this.options = Options.setter(options)._inputMode(inputMode).get();
        }

        @Override
        public Options options() {
            return options;
        }

        @Override
        public Mapping options(Function<Options, Options> setting) {
            return new FieldMapping(
                    this.options()._inputMode(),
                    this.doConvert,
                    this.moreValidate,
                    setting.apply(this.options())
                );
        }

        @Override
        public T convert(String name, Map<String, String> data) {
            return doConvert.apply(name, data);
        }

        @Override
        public List<ErrMessage> validate(String name, Map<String, String> data,
                                         Messages messages, Options parentOptions) {
            return null;
        }
    }

    public static class GroupMapping implements Mapping<BindObject> {
        private Options options;

        @Override
        public Options options() {
            return options;
        }

        @Override
        public Mapping options(Function<Options, Options> setting) {
            GroupMapping mapping = new GroupMapping();
            mapping.options = setting.apply(this.options);
            return mapping;
        }

        @Override
        public BindObject convert(String name, Map<String, String> data) {
            return null;
        }

        @Override
        public List<ErrMessage> validate(String name, Map<String, String> data,
                                         Messages messages, Options parentOptions) {
            return null;
        }
    }

}
