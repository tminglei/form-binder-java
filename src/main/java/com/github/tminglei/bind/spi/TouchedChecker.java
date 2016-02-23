package com.github.tminglei.bind.spi;

import java.util.Map;

@FunctionalInterface
public interface TouchedChecker {
    boolean apply(String prefix, Map<String, String> data);
}
