package com.github.tminglei.bind;

import java.util.*;

/**
 * object used to hold bind result or errors
 */
public class BindObject implements Iterable<Map.Entry<String, Object>> {
    static final String DEFAULT_KEY = "__default_key";

    private final Object errors;
    private final Map<String, Object> data;

    public BindObject(Object errors) {
        this.errors = errors;
        this.data = new HashMap<>();
    }
    public BindObject(Map<String, Object> data) {
        this.errors = null;
        this.data = data;
    }

    /**
     * @param <Err> expected type
     * @return the errors optional
     */
    public <Err> Optional<Err> errors() {
        return Optional.ofNullable((Err) errors);
    }

    /**
     * implementation for interface Iterable
     * @return the field iterator
     */
    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return data.entrySet().iterator();
    }

    /**
     * @return field names
     */
    public Set<String> names() {
        return data.keySet();
    }

    /**
     * @param name field name
     * @return true if exists; false if not
     */
    public boolean has(String name) {
        return data.get(name) != null;
    }

    /**
     * @param name field name
     * @return field bind object
     */
    public BindObject obj(String name) {
        return (BindObject) data.get(name);
    }

    /**
     * @param <T> expected type
     * @return the final result value
     */
    public <T> T get() {
        return (T) data.get(DEFAULT_KEY);
    }

    /**
     * @param name field name
     * @param <T> expected type
     * @return field value
     */
    public <T> T get(String name) {
        return (T) data.get(name);
    }

    ///--

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (errors != null) {
            builder.append("errors: ");
            builder.append(errors);
        } else {
            builder.append("{ ");
            data.entrySet().stream().forEach(e -> {
                builder.append(e.getKey()).append(": ").append(e.getValue()).append(", ");
            });
            builder.append(" }");
        }
        return builder.toString();
    }

}
