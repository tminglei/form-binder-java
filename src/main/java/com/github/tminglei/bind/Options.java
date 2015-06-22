package com.github.tminglei.bind;

import java.util.*;

/**
 * Created by tminglei on 6/6/15.
 */
public class Options {
    public static final Options EMPTY = new Options();

    private Boolean i18n;
    private Boolean eagerCheck;
    private Boolean ignoreEmpty;
    private List<String> touched;
    // internal options, only applied to current mapping
    private framework.InputMode _inputMode;
    private String  _label = null;
    private boolean _ignoreConstraints;
    private List<framework.Constraint> _constraints = Collections.EMPTY_LIST;
    private List<framework.PreProcessor> _processors = Collections.EMPTY_LIST;
    private List<framework.ExtraConstraint> _extraConstraints = Collections.EMPTY_LIST;

    public Options() {}
    public Options(Boolean i18n, Boolean eagerCheck, Boolean ignoreEmpty, List<String> touched) {
        this.i18n = i18n;
        this.eagerCheck = eagerCheck;
        this.ignoreEmpty = ignoreEmpty;
        this.touched = FrameworkUtils.unmodifiableList(touched);
    }

    public Options merge(Options parent) {
        return new Options(
                i18n != null ? i18n : parent.i18n,
                eagerCheck != null ? eagerCheck : parent.eagerCheck,
                ignoreEmpty != null ? ignoreEmpty : parent.ignoreEmpty,
                parent.touched
            );
    }

    public Boolean i18n() {
        return this.i18n;
    }
    public Boolean eagerCheck() {
        return this.eagerCheck;
    }
    public Boolean ignoreEmpty() {
        return this.ignoreEmpty;
    }
    public List<String> touched() {
        return this.touched;
    }

    //-- internal options
    public framework.InputMode _inputMode() {
        return this._inputMode;
    }
    public String _label() {
        return this._label;
    }
    public boolean _ignoreConstraints() {
        return this._ignoreConstraints;
    }
    public List<framework.Constraint> _constraints() {
        return this._constraints;
    }
    public List<framework.PreProcessor> _processors() {
        return this._processors;
    }
    public List<framework.ExtraConstraint> _extraConstraints() {
        return this._extraConstraints;
    }

    ///////////////////////////////////////////////////////////////////////////////

    public static OptionSetter setter(Options options) {
        return new OptionSetter(options);
    }

    public static class OptionSetter {
        private Options options;

        OptionSetter(Options options) { // clone, to keep the original unchanged
            this.options = new Options(options.i18n, options.eagerCheck, options.ignoreEmpty, options.touched);
            this.options._inputMode = options._inputMode;
            this.options._label = options._label;
            this.options._ignoreConstraints = options._ignoreConstraints;
            this.options._constraints = options._constraints;
            this.options._processors = options._processors;
        }
        public Options get() {
            return options;
        }

        public OptionSetter i18n(boolean i18n) {
            options.i18n = i18n;
            return this;
        }
        public OptionSetter eagerCheck(boolean eagerCheck) {
            options.eagerCheck = eagerCheck;
            return this;
        }
        public OptionSetter ignoreEmpty(boolean ignoreEmpty) {
            options.ignoreEmpty = ignoreEmpty;
            return this;
        }
        public OptionSetter touched(List<String> touched) {
            options.touched = FrameworkUtils.unmodifiableList(touched);
            return this;
        }

        //-- internal options
        public OptionSetter _inputMode(framework.InputMode inputMode) {
            options._inputMode = inputMode;
            return this;
        }
        public OptionSetter _label(String label) {
            options._label = label;
            return this;
        }
        public OptionSetter _ignoreConstraints(boolean ignore) {
            options._ignoreConstraints = ignore;
            return this;
        }
        ///
        public OptionSetter _constraints(List<framework.Constraint> constraints) {
            options._constraints = FrameworkUtils.unmodifiableList(constraints);
            return this;
        }
        public OptionSetter append_constraints(framework.Constraint... constraints) {
            List<framework.Constraint> tempList = new ArrayList<>(options._constraints);
            tempList.addAll(Arrays.asList(constraints));
            options._constraints = FrameworkUtils.unmodifiableList(tempList);
            return this;
        }
        public OptionSetter prepend_constraints(framework.Constraint... constraints) {
            List<framework.Constraint> tempList = new ArrayList<>(Arrays.asList(constraints));
            tempList.addAll(options._constraints);
            options._constraints = FrameworkUtils.unmodifiableList(tempList);
            return this;
        }
        ///
        public OptionSetter _processors(List<framework.PreProcessor> processors) {
            options._processors = FrameworkUtils.unmodifiableList(processors);
            return this;
        }
        public OptionSetter append_processors(framework.PreProcessor... processors) {
            List<framework.PreProcessor> tempList = new ArrayList<>(options._processors);
            tempList.addAll(Arrays.asList(processors));
            options._processors = FrameworkUtils.unmodifiableList(tempList);
            return this;
        }
        public OptionSetter prepend_processors(framework.PreProcessor... processors) {
            List<framework.PreProcessor> tempList = new ArrayList<>(Arrays.asList(processors));
            tempList.addAll(options._processors);
            options._processors = FrameworkUtils.unmodifiableList(tempList);
            return this;
        }
        ///
        public OptionSetter _extraConstraints(List<framework.ExtraConstraint> constraints) {
            options._extraConstraints = FrameworkUtils.unmodifiableList(constraints);
            return this;
        }
        public OptionSetter append_extraConstraints(framework.ExtraConstraint... constraints) {
            List<framework.ExtraConstraint> tempList = new ArrayList<>(options._extraConstraints);
            tempList.addAll(Arrays.asList(constraints));
            options._extraConstraints = FrameworkUtils.unmodifiableList(tempList);
            return this;
        }
    }
}
