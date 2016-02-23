package com.github.tminglei.bind.spi;

import com.github.tminglei.bind.Framework;

public class MappingMeta {
    public final Class<?> targetType;
    public final Framework.Mapping<?>[] baseMappings;

    public MappingMeta(Class<?> targetType, Framework.Mapping<?>... baseMappings) {
        this.targetType = targetType;
        this.baseMappings = baseMappings;
    }
}

