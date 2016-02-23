package com.github.tminglei.bind.spi;

/**
 * Created by tminglei on 2/23/16.
 */
public interface Metable<M> {
    default M meta() { return null; }
}

