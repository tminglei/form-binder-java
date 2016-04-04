package com.github.tminglei.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * The Facade class
 */
public class FormBinder<Err> {
    private final Messages messages;
    private final Function<List<Map.Entry<String, String>>, Err> errProcessor;

    private static final Logger logger = LoggerFactory.getLogger(FormBinder.class);

    public FormBinder(Messages messages) {
        this(messages, (Function<List<Map.Entry<String, String>>, Err>) PASS_THROUGH);
    }
    public FormBinder(Messages messages, Function<List<Map.Entry<String, String>>, Err> errProcessor) {
        Objects.requireNonNull(messages, "messages is NULL!!");
        Objects.requireNonNull(errProcessor, "errProcessor is NULL!!");
        this.messages = messages;
        this.errProcessor = errProcessor;
    }

    /**
     * bind mappings to data, and return an bindObject, which holding (processed) validation errors or converted value
     * @param mapping mapping
     * @param data data
     * @return bound object
     */
    public BindObject bind(Framework.Mapping<?> mapping, Map<String, String> data) {
        return bind(mapping, data, "");
    }
    public BindObject bind(Framework.Mapping<?> mapping, Map<String, String> data, String root) {
        logger.debug("start binding ... from '{}'", root);

        List<Map.Entry<String, String>> errors = mapping.validate(root, data, messages, Options.EMPTY);
        if (errors.isEmpty()) {
            Object vObj = mapping.convert(root, data);
            return vObj instanceof BindObject ? (BindObject) vObj
                    : new BindObject(newmap(entry(BindObject.DEFAULT_KEY, vObj)));
        } else {
            return new BindObject(errProcessor.apply(errors));
        }
    }

    /**
     * bind and validate data, return (processed) validation errors
     * @param mapping mapping
     * @param data data
     * @return bound object
     */
    public Optional<Err> validate(Framework.Mapping<?> mapping, Map<String, String> data) {
        return validate(mapping, data, "");
    }
    public Optional<Err> validate(Framework.Mapping<?> mapping, Map<String, String> data, String root) {
        logger.debug("start validating ... from '{}'", root);

        List<Map.Entry<String, String>> errors = mapping.validate(root, data, messages, Options.EMPTY);
        if (errors.isEmpty()) return Optional.empty();
        else {
            return Optional.of(errProcessor.apply(errors));
        }
    }
}
