package com.github.tminglei.bind;

import java.util.*;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * object used to hold options and internal status
 */
public class Options {
    public static final Options EMPTY = new Options();

    private Boolean i18n;
    private Boolean eagerCheck;
    private Boolean ignoreEmpty;
    private List<String> touched = Collections.EMPTY_LIST;
    // internal options, only applied to current mapping
    private framework.InputMode _inputMode;
    private String  _label = null;
    private boolean _ignoreConstraints = false;
    private List<framework.Constraint> _constraints = Collections.EMPTY_LIST;
    private List<framework.PreProcessor> _processors = Collections.EMPTY_LIST;

    public Options() {}
    public Options(Boolean i18n, Boolean eagerCheck, Boolean ignoreEmpty, List<String> touched) {
        this.i18n = i18n;
        this.eagerCheck = eagerCheck;
        this.ignoreEmpty = ignoreEmpty;
        this.touched = unmodifiableList(touched);
    }

    public Options merge(Options parent) {
        return new Options(
                i18n != null ? i18n : parent.i18n,
                eagerCheck != null ? eagerCheck : parent.eagerCheck,
                ignoreEmpty != null ? ignoreEmpty : parent.ignoreEmpty,
                parent.touched
            );
    }

    ///
    public Optional<Boolean> i18n() {
        return Optional.ofNullable(this.i18n);
    }
    public Options i18n(Boolean i18n) {
        Options clone = mkclone(this);
        clone.i18n = i18n;
        return clone;
    }

    public Optional<Boolean> eagerCheck() {
        return Optional.ofNullable(this.eagerCheck);
    }
    public Options eagerCheck(Boolean eagerCheck) {
        Options clone = mkclone(this);
        clone.eagerCheck = eagerCheck;
        return clone;
    }

    public Optional<Boolean> ignoreEmpty() {
        return Optional.ofNullable(this.ignoreEmpty);
    }
    public Options ignoreEmpty(Boolean ignoreEmpty) {
        Options clone = mkclone(this);
        clone.ignoreEmpty = ignoreEmpty;
        return clone;
    }

    public List<String> touched() {
        return this.touched;
    }
    public Options touched(List<String> touched) {
        Options clone = mkclone(this);
        clone.touched = unmodifiableList(touched);
        return clone;
    }

    //-- internal options
    public framework.InputMode _inputMode() {
        return this._inputMode;
    }
    public Options _inputMode(framework.InputMode inputMode) {
        Options clone = mkclone(this);
        clone._inputMode = inputMode;
        return clone;
    }

    public Optional<String> _label() {
        return Optional.ofNullable(this._label);
    }
    public Options _label(String label) {
        Options clone = mkclone(this);
        clone._label = label;
        return clone;
    }

    public boolean _ignoreConstraints() {
        return this._ignoreConstraints;
    }
    public Options _ignoreConstraints(boolean ignore) {
        Options clone = mkclone(this);
        clone._ignoreConstraints = ignore;
        return clone;
    }

    public List<framework.Constraint> _constraints() {
        return this._constraints;
    }
    public Options _constraints(List<framework.Constraint> constraints) {
        Options clone = mkclone(this);
        clone._constraints = unmodifiableList(constraints);
        return clone;
    }
    public Options append_constraints(framework.Constraint... constraints) {
        Options clone = mkclone(this);
        clone._constraints = unmodifiableList(appendList(clone._constraints, constraints));
        return clone;
    }
    public Options prepend_constraints(framework.Constraint... constraints) {
        Options clone = mkclone(this);
        clone._constraints = unmodifiableList(mergeList(Arrays.asList(constraints), clone._constraints));
        return clone;
    }

    public List<framework.PreProcessor> _processors() {
        return this._processors;
    }
    public Options _processors(List<framework.PreProcessor> processors) {
        Options clone = mkclone(this);
        clone._processors = unmodifiableList(processors);
        return clone;
    }
    public Options append_processors(framework.PreProcessor... processors) {
        Options clone = mkclone(this);
        clone._processors = unmodifiableList(appendList(clone._processors, processors));
        return clone;
    }
    public Options prepend_processors(framework.PreProcessor... processors) {
        Options clone = mkclone(this);
        clone._processors = unmodifiableList(mergeList(Arrays.asList(processors), clone._processors));
        return clone;
    }

    /////////////////////////////////////////////////////////////////////////////////////

    private Options mkclone(Options options) {
        Options clone = new Options(options.i18n, options.eagerCheck, options.ignoreEmpty, options.touched);
        clone._inputMode = options._inputMode;
        clone._label = options._label;
        clone._ignoreConstraints = options._ignoreConstraints;
        clone._constraints = options._constraints;
        clone._processors = options._processors;
        return clone;
    }
}
