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
    private Framework.TouchedChecker touched;
    // internal options, only applied to current mapping
    private Framework.InputMode _inputMode = Framework.InputMode.SINGLE;
    private String  _label = null;
    private boolean _ignoreConstraints = false;
    private List<Framework.Constraint> _constraints = Collections.EMPTY_LIST;
    private List<Framework.PreProcessor> _processors = Collections.EMPTY_LIST;

    public Options() {}
    public Options(Boolean i18n, Boolean eagerCheck, Boolean ignoreEmpty, Framework.TouchedChecker touched) {
        this.i18n = i18n;
        this.eagerCheck = eagerCheck;
        this.ignoreEmpty = ignoreEmpty;
        this.touched = touched;
    }

    public Options merge(Options parent) {
        Options clone = this.clone();
        clone.i18n = i18n != null ? i18n : parent.i18n;
        clone.eagerCheck = eagerCheck != null ? eagerCheck : parent.eagerCheck;
        clone.ignoreEmpty = ignoreEmpty != null ? ignoreEmpty : parent.ignoreEmpty;
        clone.touched = parent.touched;
        return clone;
    }

    ///
    public Optional<Boolean> i18n() {
        return Optional.ofNullable(this.i18n);
    }
    public Options i18n(Boolean i18n) {
        Options clone = this.clone();
        clone.i18n = i18n;
        return clone;
    }

    public Optional<Boolean> eagerCheck() {
        return Optional.ofNullable(this.eagerCheck);
    }
    public Options eagerCheck(Boolean eagerCheck) {
        Options clone = this.clone();
        clone.eagerCheck = eagerCheck;
        return clone;
    }

    public Optional<Boolean> ignoreEmpty() {
        return Optional.ofNullable(this.ignoreEmpty);
    }
    public Options ignoreEmpty(Boolean ignoreEmpty) {
        Options clone = this.clone();
        clone.ignoreEmpty = ignoreEmpty;
        return clone;
    }

    public Framework.TouchedChecker touched() {
        return this.touched;
    }
    public Options touched(Framework.TouchedChecker touched) {
        Options clone = this.clone();
        clone.touched = touched;
        return clone;
    }

    //-- internal options
    public Framework.InputMode _inputMode() {
        return this._inputMode;
    }
    public Options _inputMode(Framework.InputMode inputMode) {
        Options clone = this.clone();
        clone._inputMode = inputMode;
        return clone;
    }

    public Optional<String> _label() {
        return Optional.ofNullable(this._label);
    }
    public Options _label(String label) {
        Options clone = this.clone();
        clone._label = label;
        return clone;
    }

    public boolean _ignoreConstraints() {
        return this._ignoreConstraints;
    }
    public Options _ignoreConstraints(boolean ignore) {
        Options clone = this.clone();
        clone._ignoreConstraints = ignore;
        return clone;
    }

    public List<Framework.Constraint> _constraints() {
        return this._constraints;
    }
    public Options _constraints(List<Framework.Constraint> constraints) {
        Options clone = this.clone();
        clone._constraints = unmodifiableList(constraints);
        return clone;
    }
    public Options append_constraints(Framework.Constraint... constraints) {
        Options clone = this.clone();
        clone._constraints = unmodifiableList(appendList(clone._constraints, constraints));
        return clone;
    }
    public Options prepend_constraints(Framework.Constraint... constraints) {
        Options clone = this.clone();
        clone._constraints = unmodifiableList(mergeList(Arrays.asList(constraints), clone._constraints));
        return clone;
    }

    public List<Framework.PreProcessor> _processors() {
        return this._processors;
    }
    public Options _processors(List<Framework.PreProcessor> processors) {
        Options clone = this.clone();
        clone._processors = unmodifiableList(processors);
        return clone;
    }
    public Options append_processors(Framework.PreProcessor... processors) {
        Options clone = this.clone();
        clone._processors = unmodifiableList(appendList(clone._processors, processors));
        return clone;
    }
    public Options prepend_processors(Framework.PreProcessor... processors) {
        Options clone = this.clone();
        clone._processors = unmodifiableList(mergeList(Arrays.asList(processors), clone._processors));
        return clone;
    }

    /////////////////////////////////////////////////////////////////////////////////////

    public Options clone() {
        Options clone = new Options(this.i18n, this.eagerCheck, this.ignoreEmpty, this.touched);
        clone._inputMode = this._inputMode;
        clone._label = this._label;
        clone._ignoreConstraints = this._ignoreConstraints;
        clone._constraints = this._constraints;
        clone._processors = this._processors;
        return clone;
    }
}
