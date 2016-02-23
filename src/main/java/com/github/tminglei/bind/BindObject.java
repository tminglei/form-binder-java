package com.github.tminglei.bind;

import java.util.*;

/**
 * object used to hold bind result or errors
 */
public class BindObject implements Iterable<Map.Entry<String, Object>> {
    static final String DEFAULT_KEY = "_default$";

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
     * @param <T> expected type
     * @return the errors optional
     */
    public <T> Optional<T> errors() {
        return Optional.ofNullable((T) errors);
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
     * @return field bind object
     */
    public BindObject node(String name) {
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

    /**
     * @param name field name
     * @return true if exists; false if not
     */
    public boolean has(String name) {
        return data.get(name) != null;
    }

    ///--

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        if (errors != null) {
            buf.append("errors: ");
            buf.append(errors);
        } else {
            buf.append("{ ");
            data.entrySet().stream().forEach(e -> {
                buf.append(e.getKey()).append(": ").append(e.getValue()).append(", ");
            });
            buf.append(" }");
        }
        return buf.toString();
    }

}
