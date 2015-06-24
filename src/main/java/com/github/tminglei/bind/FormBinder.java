package com.github.tminglei.bind;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * The Facade class
 */
public class FormBinder {
    private final framework.Messages messages;
    private List<framework.Constraint> constraints;
    private List<framework.PreProcessor> preProcessors;
    private Function<List<Map.Entry<String, String>>, ?> errProcessor;

    public FormBinder(framework.Messages messages) {
        this(messages, Collections.EMPTY_LIST, Collections.EMPTY_LIST, null);
    }
    public FormBinder(framework.Messages messages, List<framework.Constraint> constraints,
                      List<framework.PreProcessor> preProcessors,
                      Function<List<Map.Entry<String, String>>, ?> errProcessor) {
        this.messages = messages;
        this.constraints = constraints;
        this.preProcessors = preProcessors;
        this.errProcessor = errProcessor;
    }

    ///
    public FormBinder withConstraints(framework.Constraint... constraints) {
        this.constraints = appendList(this.constraints, constraints);
        return this;
    }
    public FormBinder withPreProcessors(framework.PreProcessor... processors) {
        this.preProcessors = appendList(this.preProcessors, processors);
        return this;
    }
    public FormBinder withErrProcessor(Function<List<Map.Entry<String, String>>, ?> errProcessor) {
        this.errProcessor = errProcessor;
        return this;
    }

    ///
    /**
     * bind mappings to data, and return an bindObject, which holding (processed) validation errors or converted value
     */
    public BindObject bind(framework.Mapping<?> mapping, Map<String, String> data) {
        return bind(mapping, data, null);
    }
    public BindObject bind(framework.Mapping<?> mapping, Map<String, String> data, List<String> touched) {
        Options options = Options.EMPTY.merge(mapping.options()).touched(touched);
        Map<String, String> newData = processDataRec("", data, options, preProcessors);
        List<Map.Entry<String, String>> errors0 = validateRec("", newData, messages, options, constraints);
        List<Map.Entry<String, String>> errors1 = errors0.isEmpty() || options.eagerCheck().orElse(false)
                ? mapping.validate("", newData, messages, options)
                : Collections.EMPTY_LIST;

        List<Map.Entry<String, String>> errors = mergeList(errors0, errors1);
        if (errors.isEmpty()) {
            Object vObject = mapping.convert("", newData);
            return vObject instanceof BindObject
                    ? (BindObject) vObject
                    : new BindObject(mmap(entry(BindObject.DEFAULT_KEY, vObject)));
        } else {
            if (errProcessor != null) {
                return new BindObject(errProcessor.apply(errors));
            } else return new BindObject(errors);
        }
    }

    /**
     * bind and validate data, return (processed) validation errors
     */
    public <Err> Optional<Err> validate(framework.Mapping<?> mapping, Map<String, String> data) {
        return validate(mapping, data, null);
    }
    public <Err> Optional<Err> validate(framework.Mapping<?> mapping, Map<String, String> data, List<String> touched) {
        Options options = Options.EMPTY.merge(mapping.options()).touched(touched);
        Map<String, String> newData = processDataRec("", data, options, preProcessors);
        List<Map.Entry<String, String>> errors0 = validateRec("", newData, messages, options, constraints);
        List<Map.Entry<String, String>> errors1 = errors0.isEmpty() || options.eagerCheck().orElse(false)
                ? mapping.validate("", newData, messages, options)
                : Collections.EMPTY_LIST;

        List<Map.Entry<String, String>> errors = mergeList(errors0, errors1);
        if (errProcessor != null) {
            return Optional.ofNullable((Err) errProcessor.apply(errors));
        } else return Optional.ofNullable((Err) errors);
    }
}
