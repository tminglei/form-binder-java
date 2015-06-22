package com.github.tminglei.bind;

import java.util.Map;

/**
 * Created by tminglei on 6/21/15.
 */
public class BindObject {
    private final Map<String, Object> data;

    BindObject(Map<String, Object> data) {
        this.data = data;
    }

    public BindObject node(String name) {
        checkExisted(name);
        return (BindObject) data.get(name);
    }
    public <T> T get(String name) {
        checkExisted(name);
        return (T) data.get(name);
    }

    //////////////////////////////////////////
    private void checkExisted(String name) {
        if (!data.containsKey(name)) throw new IllegalArgumentException("Undefined name: " + name);
    }
}
