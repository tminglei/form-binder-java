package com.github.tminglei.bind;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * object used to hold bind result or errors
 */
public class BindObject {
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

    public <T> Optional<T> errors() {
        return Optional.ofNullable((T) errors);
    }

    public BindObject node(String name) {
        checkExisted(name);
        return (BindObject) data.get(name);
    }
    public <T> T get() {
        checkExisted(DEFAULT_KEY);
        return (T) data.get(DEFAULT_KEY);
    }
    public <T> T get(String name) {
        checkExisted(name);
        return (T) data.get(name);
    }

    // build a bean based on data of this bind object
    public <T> T build(Class<T> clazz) {
        return null;//todo
    }

    //////////////////////////////////////////
    private void checkExisted(String name) {
        if (!data.containsKey(name)) throw new IllegalArgumentException("Undefined name: " + name);
    }
}
