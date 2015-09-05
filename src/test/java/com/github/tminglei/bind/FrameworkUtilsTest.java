package com.github.tminglei.bind;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class FrameworkUtilsTest {

    @BeforeClass
    public void start() {
        System.out.println(cyan("test utility methods of FrameworkUtils"));
    }

    @Test
    public void testIsEmptyInput() {
        System.out.println(green(">> is empty input"));

        Map<String, String> data = newmap(
                entry("a", null),
                entry("b", "t"),
                entry("c.t", "xxx")
        );

        assertEquals(FrameworkUtils.isEmptyInput("a", data, null), true);
        assertEquals(FrameworkUtils.isEmptyInput("a", data, InputMode.SINGLE), true);
        assertEquals(FrameworkUtils.isEmptyInput("a", data, InputMode.MULTIPLE), true);
        assertEquals(FrameworkUtils.isEmptyInput("b", data, InputMode.SINGLE), false);
        assertEquals(FrameworkUtils.isEmptyInput("b", data, InputMode.MULTIPLE), true);
        assertEquals(FrameworkUtils.isEmptyInput("b", data, InputMode.POLYMORPHIC), false);
        assertEquals(FrameworkUtils.isEmptyInput("c", data, null), false);
        assertEquals(FrameworkUtils.isEmptyInput("c", data, InputMode.MULTIPLE), false);
        assertEquals(FrameworkUtils.isEmptyInput("c", data, InputMode.POLYMORPHIC), false);
    }

    @Test
    public void testSplitName() {
        System.out.println(green(">> split name"));

        String[] parts1 = FrameworkUtils.splitName("a.b.c");
        assertEquals(parts1[0], "a.b");
        assertEquals(parts1[1], "c");
        assertEquals(parts1[2], "false");

        String[] parts2 = FrameworkUtils.splitName("a.b.c[1]");
        assertEquals(parts2[0], "a.b.c");
        assertEquals(parts2[1], "1");
        assertEquals(parts2[2], "true");
    }

    @Test
    public void testGetLabel() {
        System.out.println(green(">> get label"));

        assertEquals(FrameworkUtils.getLabel("a.b.c", (key) -> "tt", Options.EMPTY),
                "c");
        assertEquals(FrameworkUtils.getLabel("a.b.c", (key) -> "tt", Options.EMPTY._label("t1")),
                "t1");
        assertEquals(FrameworkUtils.getLabel("a.b.c", (key) -> "tt", Options.EMPTY.i18n(true)._label("t1")),
                "tt");
        assertEquals(FrameworkUtils.getLabel("a.b.c", (key) -> "tt", Options.EMPTY.i18n(true)),
                "c");
        assertEquals(FrameworkUtils.getLabel("a.b.c", (key) -> null, Options.EMPTY.i18n(true)._label("t1")),
                "t1");

        assertEquals(FrameworkUtils.getLabel("a.b.c[1]", (key) -> "tt", Options.EMPTY),
                "c[1]");
    }

    @Test
    public void testIndexes() {
        System.out.println(green(">> indexes"));

        Map<String, String> data = newmap(
                entry("a", "tt"),
                entry("b[3]", "tx"),
                entry("b[0]", "t1"),
                entry("b[1]", "ts")
        );

        assertEquals(FrameworkUtils.indexes("b", data),
                Arrays.asList(0, 1, 3));
        assertEquals(FrameworkUtils.indexes("a", data),
                Collections.EMPTY_LIST);
    }

    @Test
    public void testKeys() {
        System.out.println(green(">> keys"));

        Map<String, String> data = newmap(
                entry("a.b", "te"),
                entry("a.t", "tt"),
                entry("c", "tew")
        );

        assertEquals(FrameworkUtils.keys("a", data),
                Arrays.asList("b", "t"));
        assertEquals(FrameworkUtils.keys("c", data),
                Collections.EMPTY_LIST);
    }

    @Test
    public void testJson2map() throws IOException {
        System.out.println(green(">> json2map"));

        JsonNode json = new ObjectMapper().readTree(
                "{\"data\": {\"email\":null, \"price\":337.5, \"count\":5}, \"touched\": [\"email\", \"price\"]}");

        Map<String, String> expected1 = newmap(
                entry("data.email", "null"),
                entry("data.price", "337.5"),
                entry("data.count", "5"),
                entry("touched[0]", "email"),
                entry("touched[1]", "price")
        );
        assertEquals(FrameworkUtils.json2map("", json), expected1);

        Map<String, String> expected2 = newmap(
                entry("t.data.email", "null"),
                entry("t.data.price", "337.5"),
                entry("t.data.count", "5"),
                entry("t.touched[0]", "email"),
                entry("t.touched[1]", "price")
        );
        assertEquals(FrameworkUtils.json2map("t", json), expected2);
    }
}
