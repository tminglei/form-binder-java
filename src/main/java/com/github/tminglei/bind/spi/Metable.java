package com.github.tminglei.bind.spi;

public interface Metable<M> {
    default M meta() { return null; }
}

