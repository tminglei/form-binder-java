package com.github.tminglei.bind.spi;

import java.util.List;

import static com.github.tminglei.bind.FrameworkUtils.unmodifiableList;

/**
 * Created by tminglei on 2/23/16.
 */
public class ExtensionMeta {
    public final String name;
    public final String desc;
    public final List<?> params;

    public ExtensionMeta(String name, String desc, List<?> params) {
        this.name = name;
        this.desc = desc;
        this.params = unmodifiableList(params);
    }
}

