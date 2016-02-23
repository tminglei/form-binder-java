package com.github.tminglei.bind.spi;

import com.github.tminglei.bind.api.Messages;

import java.util.List;
import java.util.Map;

/**
 * Created by tminglei on 2/23/16.
 */
@FunctionalInterface
public interface Constraint extends Metable<ExtensionMeta> {
    List<Map.Entry<String, String>> apply(String name, Map<String, String> data, Messages messages, Options options);
}

