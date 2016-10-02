package com.github.tminglei.bind.spi;

import com.github.tminglei.bind.Framework;

public class MappingMeta {
    public final String name;
    public final Class<?> targetType;
    public final Framework.Mapping<?>[] baseMappings;

    public MappingMeta(String name, Class<?> targetType, Framework.Mapping<?>... baseMappings) {
        this.name = name;
        this.targetType = targetType;
        this.baseMappings = baseMappings;
    }
}

