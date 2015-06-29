package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Map;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class TouchedCheckersTest {

    @BeforeClass
    public void start() {
        System.out.println(cyan("test pre-defined touched checkers"));
    }

    @Test
    public void testListTouchedChecker() {
        System.out.println(green(">> list-based touched checker"));

        TouchedChecker checker = Processors.listTouched(
                Arrays.asList("data.email", "data.price", "id")
            );

        assertEquals(checker.apply("nonexist", mmap()), false);
        assertEquals(checker.apply("id", mmap()), true);
        assertEquals(checker.apply("data", mmap()), true);
    }

    @Test
    public void testPrefixTouchedChecker() {
        System.out.println(green(">> prefix-based touched checker"));

        TouchedChecker checker = Processors.prefixTouched("data", "touched");
        Map<String, String> data = mmap(
                entry("id", "tee"),
                entry("data.email", null),
                entry("data.price", "135"),
                entry("touched.email", "true")
            );

        assertEquals(checker.apply("c", data), false);
        assertEquals(checker.apply("data.email", data), true);
        assertEquals(checker.apply("data.price", data), false);
    }
}
