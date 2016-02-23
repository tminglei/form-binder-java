package com.github.tminglei.bind.spi;

import java.util.Map;

/**
 * Created by tminglei on 2/23/16.
 */
@FunctionalInterface
public interface PreProcessor extends Metable<ExtensionMeta> {
    Map<String, String> apply(String prefix, Map<String, String> data, Options options);
}
