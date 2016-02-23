package com.github.tminglei.bind.spi;

import java.util.List;

import com.github.tminglei.bind.Messages;

/**
 * Created by tminglei on 2/23/16.
 */
@FunctionalInterface
public interface ExtraConstraint<T> extends Metable<ExtensionMeta> {
    List<String> apply(String label, T vObj, Messages messages);
}
