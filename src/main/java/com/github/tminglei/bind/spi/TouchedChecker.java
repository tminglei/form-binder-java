package com.github.tminglei.bind.spi;

import java.util.Map;

/**
 * Created by tminglei on 2/23/16.
 */
@FunctionalInterface
public interface TouchedChecker {
    boolean apply(String prefix, Map<String, String> data);
}
