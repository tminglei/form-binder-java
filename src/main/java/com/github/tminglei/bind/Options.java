package com.github.tminglei.bind;

import java.util.*;
import java.util.stream.Collectors;

import com.github.tminglei.bind.spi.*;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * object used to hold options and internal status
 */
public class Options {
    public static final Options EMPTY = new Options();

    private Boolean eagerCheck;
    private Boolean skipUntouched;
    private TouchedChecker touchedChecker;
    // internal state, only applied to current mapping
    private InputMode inputMode;
    private String label = null;
    private boolean ignoreConstraints = false;
    private List<Constraint> constraints = Collections.emptyList();
    private List<ExtraConstraint<?>> extraConstraints = Collections.emptyList();
    private List<PreProcessor> processors = Collections.emptyList();
    // used to associate/hold application specific object
    private Object attachment;

    public Options() {}
    public Options(Boolean eagerCheck, Boolean skipUntouched, TouchedChecker touchedChecker) {
        this.eagerCheck = eagerCheck;
        this.skipUntouched = skipUntouched;
        this.touchedChecker = touchedChecker;
    }

    public Options merge(Options other) {
        Options clone = this.clone();
        clone.eagerCheck = eagerCheck != null ? eagerCheck : other.eagerCheck;
        clone.skipUntouched = skipUntouched != null ? skipUntouched : other.skipUntouched;
        clone.touchedChecker = touchedChecker != null ? touchedChecker : other.touchedChecker;
        return clone;
    }

    ///

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
     * whether to skip checking untouched empty field/values
     * @return the value optional
     */
    public Optional<Boolean> skipUntouched() {
        return Optional.ofNullable(this.skipUntouched);
    }
    public Options skipUntouched(Boolean ignoreEmpty) {
        Options clone = this.clone();
        clone.skipUntouched = ignoreEmpty;
        return clone;
    }

    /**
     * used to check whether a field was touched by user
     * @return the touched checker
     */
    public TouchedChecker touchedChecker() {
        return this.touchedChecker;
    }
    public Options touchedChecker(TouchedChecker touched) {
        Options clone = this.clone();
        clone.touchedChecker = touched;
        return clone;
    }

    //-- internal options
    InputMode _inputMode() {
        return this.inputMode;
    }
    Options _inputMode(InputMode inputMode) {
        Options clone = this.clone();
        clone.inputMode = inputMode;
        return clone;
    }

    Optional<String> _label() {
        return Optional.ofNullable(this.label);
    }
    Options _label(String label) {
        Options clone = this.clone();
        clone.label = label;
        return clone;
    }

    boolean _ignoreConstraints() {
        return this.ignoreConstraints;
    }
    Options _ignoreConstraints(boolean ignore) {
        Options clone = this.clone();
        clone.ignoreConstraints = ignore;
        return clone;
    }

    List<Constraint> _constraints() {
        return this.constraints;
    }
    Options _constraints(List<Constraint> constraints) {
        Options clone = this.clone();
        clone.constraints = unmodifiableList(constraints);
        return clone;
    }
    Options append_constraints(List<Constraint> constraints) {
        Options clone = this.clone();
        clone.constraints = unmodifiableList(mergeList(clone.constraints, constraints));
        return clone;
    }
    Options prepend_constraints(List<Constraint> constraints) {
        Options clone = this.clone();
        clone.constraints = unmodifiableList(mergeList(constraints, clone.constraints));
        return clone;
    }

    List<PreProcessor> _processors() {
        return this.processors;
    }
    Options _processors(List<PreProcessor> processors) {
        Options clone = this.clone();
        clone.processors = unmodifiableList(processors);
        return clone;
    }
    Options append_processors(List<PreProcessor> processors) {
        Options clone = this.clone();
        clone.processors = unmodifiableList(mergeList(clone.processors, processors));
        return clone;
    }
    Options prepend_processors(List<PreProcessor> processors) {
        Options clone = this.clone();
        clone.processors = unmodifiableList(mergeList(processors, clone.processors));
        return clone;
    }

    <T> List<ExtraConstraint<T>> _extraConstraints() {
        return this.extraConstraints.stream().map(c -> (ExtraConstraint<T>) c).collect(Collectors.toList());
    }
    Options _extraConstraints(List<ExtraConstraint<?>> extraConstraints) {
        Options clone = this.clone();
        clone.extraConstraints = unmodifiableList(extraConstraints);
        return clone;
    }
    Options append_extraConstraints(List<ExtraConstraint<?>> extraConstraints) {
        Options clone = this.clone();
        clone.extraConstraints = unmodifiableList(mergeList(clone.extraConstraints, extraConstraints));
        return clone;
    }

    ///
    Object _attachment() {
        return this.attachment;
    }
    Options _attachment(Object attachment) {
        Options clone = this.clone();
        clone.attachment = attachment;
        return clone;
    }

    /////////////////////////////////////////////////////////////////////////////////////

    protected Options clone() {
        Options clone = new Options(this.eagerCheck, this.skipUntouched, this.touchedChecker);
        clone.inputMode = this.inputMode;
        clone.label = this.label;
        clone.ignoreConstraints = this.ignoreConstraints;
        clone.constraints = this.constraints;
        clone.extraConstraints = this.extraConstraints;
        clone.processors = this.processors;
        clone.attachment = this.attachment;
        return clone;
    }
}
