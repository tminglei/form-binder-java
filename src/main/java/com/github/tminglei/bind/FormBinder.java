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
    private final Framework.Messages messages;
    private List<Framework.Constraint> constraints;
    private List<Framework.PreProcessor> preProcessors;
    private Function<List<Map.Entry<String, String>>, ?> errProcessor;

    public FormBinder(Framework.Messages messages) {
        this(messages, Collections.EMPTY_LIST, Collections.EMPTY_LIST, null);
    }
    public FormBinder(Framework.Messages messages, List<Framework.Constraint> constraints,
                      List<Framework.PreProcessor> preProcessors,
                      Function<List<Map.Entry<String, String>>, ?> errProcessor) {
        this.messages = messages;
        this.constraints = constraints;
        this.preProcessors = preProcessors;
        this.errProcessor = errProcessor;
    }

    ///
    public FormBinder withConstraints(Framework.Constraint... constraints) {
        this.constraints = appendList(this.constraints, constraints);
        return this;
    }
    public FormBinder withPreProcessors(Framework.PreProcessor... processors) {
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
    public BindObject bind(Framework.Mapping<?> mapping, Map<String, String> data) {
        return bind(mapping, data, "");
    }
    public BindObject bind(Framework.Mapping<?> mapping, Map<String, String> data, String root) {
        Options options = Options.EMPTY.merge(mapping.options());
        Map<String, String> newData = processDataRec(root, data, options, preProcessors);
        List<Map.Entry<String, String>> errors0 = validateRec(root, newData, messages, options, constraints);
        List<Map.Entry<String, String>> errors1 = errors0.isEmpty() || options.eagerCheck().orElse(false)
                ? mapping.validate(root, newData, messages, options)
                : Collections.EMPTY_LIST;

        List<Map.Entry<String, String>> errors = mergeList(errors0, errors1);
        if (errors.isEmpty()) {
            Object vObject = mapping.convert(root, newData);
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
    public <Err> Optional<Err> validate(Framework.Mapping<?> mapping, Map<String, String> data) {
        return validate(mapping, data, "");
    }
    public <Err> Optional<Err> validate(Framework.Mapping<?> mapping, Map<String, String> data, String root) {
        Options options = Options.EMPTY.merge(mapping.options());
        Map<String, String> newData = processDataRec(root, data, options, preProcessors);
        List<Map.Entry<String, String>> errors0 = validateRec(root, newData, messages, options, constraints);
        List<Map.Entry<String, String>> errors1 = errors0.isEmpty() || options.eagerCheck().orElse(false)
                ? mapping.validate(root, newData, messages, options)
                : Collections.EMPTY_LIST;

        List<Map.Entry<String, String>> errors = mergeList(errors0, errors1);
        if (errProcessor != null) {
            return Optional.ofNullable((Err) errProcessor.apply(errors));
        } else return Optional.ofNullable((Err) errors);
    }
}
