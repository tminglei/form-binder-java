package com.github.tminglei.bind;

import java.util.*;
import java.util.stream.Collectors;

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
    private Framework.InputMode _inputMode;
    private String  _label = null;
    private boolean _ignoreConstraints = false;
    private List<Framework.Constraint> _constraints = Collections.EMPTY_LIST;
    private List<Framework.ExtraConstraint<?>> _extraConstraints = Collections.EMPTY_LIST;
    private List<Framework.PreProcessor> _processors = Collections.EMPTY_LIST;
    // extension object
    private Framework.Extensible _ext;

    public Options() {}
    public Options(Boolean i18n, Boolean eagerCheck, Boolean ignoreEmpty, Framework.TouchedChecker touched) {
        this.i18n = i18n;
        this.eagerCheck = eagerCheck;
        this.ignoreEmpty = ignoreEmpty;
        this.touched = touched;
    }

    public Options merge(Options other) {
        Options clone = this.clone();
        clone.i18n = i18n != null ? i18n : other.i18n;
        clone.eagerCheck = eagerCheck != null ? eagerCheck : other.eagerCheck;
        clone.ignoreEmpty = ignoreEmpty != null ? ignoreEmpty : other.ignoreEmpty;
        clone.touched = touched != null ? touched : other.touched;
        return clone;
    }

    ///

    /**
     * whether activate i18n support
     * @return the value optional
     */
    public Optional<Boolean> i18n() {
        return Optional.ofNullable(this.i18n);
    }
    public Options i18n(Boolean i18n) {
        Options clone = this.clone();
        clone.i18n = i18n;
        return clone;
    }

    /**
     * whether check errors as more as possible
     * @return the value optional
     */
    public Optional<Boolean> eagerCheck() {
        return Optional.ofNullable(this.eagerCheck);
    }
    public Options eagerCheck(Boolean eagerCheck) {
        Options clone = this.clone();
        clone.eagerCheck = eagerCheck;
        return clone;
    }

    /**
     * whether skip to check empty even they are declared as required
     * @return the value optional
     */
    public Optional<Boolean> ignoreEmpty() {
        return Optional.ofNullable(this.ignoreEmpty);
    }
    public Options ignoreEmpty(Boolean ignoreEmpty) {
        Options clone = this.clone();
        clone.ignoreEmpty = ignoreEmpty;
        return clone;
    }

    /**
     * for touched fields, an error will be filed if they are declared as required and they are empty
     * @return the touched checker
     */
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
    public Options append_constraints(List<Framework.Constraint> constraints) {
        Options clone = this.clone();
        clone._constraints = unmodifiableList(mergeList(clone._constraints, constraints));
        return clone;
    }
    public Options prepend_constraints(List<Framework.Constraint> constraints) {
        Options clone = this.clone();
        clone._constraints = unmodifiableList(mergeList(constraints, clone._constraints));
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
    public Options append_processors(List<Framework.PreProcessor> processors) {
        Options clone = this.clone();
        clone._processors = unmodifiableList(mergeList(clone._processors, processors));
        return clone;
    }
    public Options prepend_processors(List<Framework.PreProcessor> processors) {
        Options clone = this.clone();
        clone._processors = unmodifiableList(mergeList(processors, clone._processors));
        return clone;
    }

    public <T> List<Framework.ExtraConstraint<T>> _extraConstraints() {
        return this._extraConstraints.stream().map(c -> (Framework.ExtraConstraint<T>) c).collect(Collectors.toList());
    }
    public Options _extraConstraints(List<Framework.ExtraConstraint<?>> extraConstraints) {
        Options clone = this.clone();
        clone._extraConstraints = unmodifiableList(extraConstraints);
        return clone;
    }
    public Options append_extraConstraints(List<Framework.ExtraConstraint<?>> extraConstraints) {
        Options clone = this.clone();
        clone._extraConstraints = unmodifiableList(mergeList(clone._extraConstraints, extraConstraints));
        return clone;
    }

    ///
    public Framework.Extensible _ext() {
        return this._ext;
    }
    public Options _ext(Framework.Extensible ext) {
        Options clone = this.clone();
        clone._ext = ext;
        return clone;
    }

    /////////////////////////////////////////////////////////////////////////////////////

    protected Options clone() {
        Options clone = new Options(this.i18n, this.eagerCheck, this.ignoreEmpty, this.touched);
        clone._inputMode = this._inputMode;
        clone._label = this._label;
        clone._ignoreConstraints = this._ignoreConstraints;
        clone._constraints = this._constraints;
        clone._extraConstraints = this._extraConstraints;
        clone._processors = this._processors;
        clone._ext = this._ext;
        return clone;
    }
}
