package com.github.tminglei.bind.spi;

import com.github.tminglei.bind.Framework;

/**
 * Created by tminglei on 2/23/16.
 */
public class MappingMeta {
    public final Class<?> targetType;
    public final Framework.Mapping<?>[] baseMappings;

    public MappingMeta(Class<?> targetType, Framework.Mapping<?>... baseMappings) {
        this.targetType = targetType;
        this.baseMappings = baseMappings;
    }
}

