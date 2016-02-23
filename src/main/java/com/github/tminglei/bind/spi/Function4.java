package com.github.tminglei.bind.spi;

/**
 * Created by tminglei on 2/23/16.
 */
@FunctionalInterface
public interface Function4<T1,T2,T3,T4,R> {
    R apply(T1 p1, T2 p2, T3 p3, T4 p4);
}
