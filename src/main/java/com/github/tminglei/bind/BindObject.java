package com.github.tminglei.bind;

import java.util.*;

/**
 * object used to hold bind result or errors
 */
public class BindObject implements Iterable<Map.Entry<String, Object>> {
    static final String DEFAULT_KEY = "_default$";

    private final Object errors;
    private final Map<String, Object> data;

    BindObject(Object errors) {
        this.errors = errors;
        this.data = new HashMap<>();
    }
    BindObject(Map<String, Object> data) {
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

}
