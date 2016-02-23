package com.github.tminglei.bind.spi;

/**
 * mark interface, used to distinguish extension object from other
 */
public interface Extensible extends java.lang.Cloneable {
    Extensible clone();
}
