package com.github.tminglei.bind.spi;

import java.util.List;
import java.util.Map;

import com.github.tminglei.bind.Options;
import com.github.tminglei.bind.Messages;

@FunctionalInterface
public interface Constraint extends Metable<ExtensionMeta> {
    List<Map.Entry<String, String>> apply(
            String name, Map<String, String> data, Messages messages, Options options);
}

