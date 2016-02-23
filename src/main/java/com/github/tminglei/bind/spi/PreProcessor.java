package com.github.tminglei.bind.spi;

import java.util.Map;

import com.github.tminglei.bind.Options;

@FunctionalInterface
public interface PreProcessor extends Metable<ExtensionMeta> {
    Map<String, String> apply(String prefix, Map<String, String> data, Options options);
}
