package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Mappings.*;
import static com.github.tminglei.bind.Constraints.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class GroupMappingsTest {
    private Messages dummyMessages = (key -> "%s dummy");

    private Mapping<BindObject> mapping1 = mapping(
            fb("count").to(vInt())
        ).label("xx")
            .verifying((label, vObj, messages) -> {
                int count = vObj.get("count");
                if (count < 3) return Arrays.asList("count cannot less than 3");
                else if (count > 10) return Arrays.asList("count cannot greater then 10");
                else return Collections.EMPTY_LIST;
            });
    private Mapping<BindObject> mapping2 = mapping(
            fb("price").to(vFloat()),
            fb("count").to(vInt().verifying(min(3), max(10)))
        ).label("xx")
            .verifying((label, vObj, messages) -> {
                float price = vObj.get("price");
                int count = vObj.get("count");
                if (price * count > 1000) return Arrays.asList("total cost too much!");
                else return Collections.EMPTY_LIST;
            });

    @BeforeClass
    public void start() {
        System.out.println(cyan("test pre-defined group mapping"));
    }

    // group mapping 1 test

    @Test
    public void testGroupMapping1_InvalidData() {
        System.out.println(green(">> group mapping 1 - invalid data"));

        Map<String, String> data = mmap(entry("count", "t5"));

        assertEquals(mapping1.validate("", data, dummyMessages, Options.EMPTY),
                Arrays.asList(entry("count", "count dummy")));
    }

    @Test
    public void testGroupMapping1_ValidData() {
        System.out.println(green(">> group mapping 1 - valid data"));

        Map<String, String> data = mmap(entry("count", "5"));

        assertEquals(mapping1.validate("", data, dummyMessages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping1.convert("", data).get("count"), Integer.valueOf(5));
    }

    @Test
    public void testGroupMapping1_OutOfScopeData() {
        System.out.println(green(">> group mapping 1 - out of scope data"));

        Map<String, String> data = mmap(entry("count", "15"));

        assertEquals(mapping1.validate("", data, dummyMessages, Options.EMPTY),
                Arrays.asList(entry("xx", "count cannot greater then 10")));
    }

    @Test
    public void testGroupMapping1_NullData() {
        System.out.println(green(">> group mapping 1 - null data"));

        Map<String, String> data = mmap();

        assertEquals(mapping1.validate("", data, dummyMessages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping1.convert("", data), null);
    }

    @Test
    public void testGroupMapping1_EmptyData() {
        System.out.println(green(">> group mapping 1 - empty data"));

        Map<String, String> data = mmap(entry("", ""));

        assertEquals(mapping1.validate("", data, dummyMessages, Options.EMPTY),
                Arrays.asList(entry("xx", "count cannot less than 3")));
        assertEquals(mapping1.convert("", data).get("count"), Integer.valueOf(0));
    }

    // group mapping 2 test

    @Test
    public void testGroupMapping2_InvalidData() {
        System.out.println(green(">> group mapping 2 - invalid data"));

        Map<String, String> data = mmap(entry("price", "23.5f"), entry("count", "t5"));

        assertEquals(mapping2.validate("", data, dummyMessages, Options.EMPTY),
                Arrays.asList(entry("count", "count dummy")));
    }

    @Test
    public void testGroupMapping2_ValidData() {
        System.out.println(green(">> group mapping 2 - valid data"));

        Map<String, String> data = mmap(entry("price", "23.5f"), entry("count", "5"));

        assertEquals(mapping2.validate("", data, dummyMessages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping2.convert("", data).get("price"), Float.valueOf(23.5f));
        assertEquals(mapping2.convert("", data).get("count"), Integer.valueOf(5));
    }

    @Test
    public void testGroupMapping2_OutOfScopeData1() {
        System.out.println(green(">> group mapping 2 - out of scope data 1"));

        Map<String, String> data = mmap(entry("price", "23.5f"), entry("count", "15"));

        assertEquals(mapping2.validate("", data, dummyMessages, Options.EMPTY),
                Arrays.asList(entry("count", "count dummy")));
    }

    @Test
    public void testGroupMapping2_OutOfScopeData2() {
        System.out.println(green(">> group mapping 2 - out of scope data 2"));

        Map<String, String> data = mmap(entry("price", "123.5f"), entry("count", "9"));

        assertEquals(mapping2.validate("", data, dummyMessages, Options.EMPTY),
                Arrays.asList(entry("xx", "total cost too much!")));
    }

    @Test
    public void testGroupMapping2_NullData() {
        System.out.println(green(">> group mapping 2 - null data"));

        Map<String, String> data = mmap();

        assertEquals(mapping2.validate("", data, dummyMessages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping2.convert("", data), null);
    }

    @Test
    public void testGroupMapping2_EmptyData() {
        System.out.println(green(">> group mapping 2 - empty data"));

        Map<String, String> data = mmap(entry("count", "9"));

        assertEquals(mapping2.validate("", data, dummyMessages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping2.convert("", data).get("price"), Float.valueOf(0.0f));
        assertEquals(mapping2.convert("", data).get("count"), Integer.valueOf(9));
    }

    // group mapping with options test

}
