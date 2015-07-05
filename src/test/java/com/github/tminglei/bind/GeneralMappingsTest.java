package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class GeneralMappingsTest {
    private ResourceBundle bundle = ResourceBundle.getBundle("bind-messages");
    private Messages messages = (key) -> bundle.getString(key);

    @BeforeClass
    public void start() {
        System.out.println(cyan("test pre-defined general mappings"));
    }

    // ignored test

    @Test
    public void testIgnored_Simple() {
        System.out.println(green(">> ignored - simple"));

        Mapping<Integer> ignored = Mappings.ignored(35);

        Map<String, String> data1 = mmap(
                entry("number", "t135")
            );
        assertEquals(ignored.validate("number", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(ignored.convert("number", data1), Integer.valueOf(35));

        Map<String, String> data2 = mmap(entry("number", "135"));
        assertEquals(ignored.validate("number", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(ignored.convert("number", data2), Integer.valueOf(35));
    }

    @Test
    public void testIgnored_Compound() {
        System.out.println(green(">> ignored - compound"));

        Mapping<BindObject> ignored = Mappings.ignored(new BindObject(
                mmap(entry("id", 135), entry("name", "ttest"))));

        Map<String, String> data1 = mmap(
                entry("id", "t135"),
                entry("tt", "teee")
            );
        assertEquals(ignored.validate("", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(ignored.convert("", data1).get("id"), Integer.valueOf(135));
        assertEquals(ignored.convert("", data1).get("name"), "ttest");

        Map<String, String> data2 = mmap();
        assertEquals(ignored.validate("", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(ignored.convert("", data2).get("id"), Integer.valueOf(135));
        assertEquals(ignored.convert("", data2).get("name"), "ttest");
    }

    // default value test

    @Test
    public void testDefaultVal_Simple() {
        System.out.println(green(">> default value - simple"));

        Mapping<Integer> defaultVal = Mappings.defaultVal(Mappings.vInt(), 101);

        Map<String, String> data1 = mmap(
                entry("number", "t122345")
            );
        assertEquals(defaultVal.validate("number", data1, messages, Options.EMPTY),
                Arrays.asList(entry("number", "'t122345' must be a number.")));

        Map<String, String> data2 = mmap(entry("number", "122345"));
        assertEquals(defaultVal.validate("number", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(defaultVal.convert("number", data2), Integer.valueOf(122345));

        Map<String, String> data3 = mmap();
        assertEquals(defaultVal.validate("number", data3, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(defaultVal.convert("number", data3), Integer.valueOf(101));
    }

    @Test
    public void testDefaultVal_Compound() {
        System.out.println(green(">> default value - compound"));

        Mapping<BindObject> defaultVal = Mappings.defaultVal(mapping(
                field("id", Mappings.vInt()),
                field("name", Mappings.text())
        ), new BindObject(mmap(entry("id", 101), entry("name", "haha"))));

        Map<String, String> data1 = mmap(
                entry("id", "1123"),
                entry("name", "tttt")
            );
        assertEquals(defaultVal.validate("", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(defaultVal.convert("", data1).get("id"), Integer.valueOf(1123));
        assertEquals(defaultVal.convert("", data1).get("name"), "tttt");

        Map<String, String> data2 = mmap();
        assertEquals(defaultVal.validate("", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(defaultVal.convert("", data2).get("id"), Integer.valueOf(101));
        assertEquals(defaultVal.convert("", data2).get("name"), "haha");
    }

    // optional test

    @Test
    public void testOptional_Simple() {
        System.out.println(green(">> optional - simple"));

        Mapping<Optional<Integer>> optional = Mappings.optional(Mappings.vInt());

        Map<String, String> data1 = mmap();
        assertEquals(optional.validate("number", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(optional.convert("number", data1), Optional.<Integer>empty());

        Map<String, String> data2 = mmap(entry("number", "101"));
        assertEquals(optional.validate("number", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(optional.convert("number", data2), Optional.of(101));
    }

    @Test
    public void testOptional_Compound() {
        System.out.println(green(">> optional - compound"));

        Mapping<Optional<BindObject>> optional = Mappings.optional(mapping(
                field("id", Mappings.vInt()),
                field("name", Mappings.text())
            ));

        Map<String, String> data1 = mmap();
        assertEquals(optional.validate("", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(optional.convert("", data1), Optional.<BindObject>empty());

        Map<String, String> data2 = mmap(entry("id", "111"), entry("name", "tt"));
        assertEquals(optional.validate("", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(optional.convert("", data2).get().get("id"), Integer.valueOf(111));
        assertEquals(optional.convert("", data2).get().get("name"), "tt");
    }

    // list test

    @Test
    public void testList_Simple() {
        System.out.println(green(">> list - simple"));

        Mapping<List<Integer>> list = Mappings.list(Mappings.vInt());

        Map<String, String> data1 = mmap(
                entry("list[1]", "101"),
                entry("list[0]", "100")
            );
        assertEquals(list.validate("list", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(list.convert("list", data1), Arrays.asList(100, 101));

        Map<String, String> data2 = mmap();
        assertEquals(list.validate("list", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(list.convert("list", data2), Collections.EMPTY_LIST);
    }

    @Test
    public void testList_Compound() {
        System.out.println(green(">> list - compound"));

        Mapping<List<BindObject>> list = Mappings.list(mapping(
                field("id", Mappings.vInt()),
                field("name", Mappings.text())
            ));

        Map<String, String> data1 = mmap(
                entry("list[1].id", "123"),
                entry("list[1].name", "ttt")
            );
        assertEquals(list.validate("list", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(list.convert("list", data1).get(0).get("id"), Integer.valueOf(123));
        assertEquals(list.convert("list", data1).get(0).get("name"), "ttt");

        Map<String, String> data2 = mmap();
        assertEquals(list.validate("list", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(list.convert("list", data2), Collections.EMPTY_LIST);
    }

    // map test

    @Test
    public void testMap_Simple() {
        System.out.println(green(">> map - simple"));

        Mapping<Map<String, Integer>> map = Mappings.map(Mappings.vInt());

        Map<String, String> data1 = mmap(
                entry("map.t1", "101"),
                entry("map.t2", "102")
            );
        assertEquals(map.validate("map", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(map.convert("map", data1), mmap(entry("t1", 101), entry("t2", 102)));

        Map<String, String> data2 = mmap();
        assertEquals(map.validate("map", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(map.convert("map", data2), Collections.EMPTY_MAP);
    }

    @Test
    public void testMap_Compound() {
        System.out.println(green(">> map - compound"));

        Mapping<Map<String, BindObject>> map = Mappings.map(mapping(
                field("id", Mappings.vInt()),
                field("name", Mappings.text())
            ));

        Map<String, String> data1 = mmap(
                entry("map.t1.id", "113"),
                entry("map.t1.name", "ttt"),
                entry("map.t2.id", "114"),
                entry("map.t2.name", "tttt")
            );
        assertEquals(map.validate("map", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(map.convert("map", data1).get("t1").get("id"), Integer.valueOf(113));
        assertEquals(map.convert("map", data1).get("t1").get("name"), "ttt");
        assertEquals(map.convert("map", data1).get("t2").get("id"), Integer.valueOf(114));
        assertEquals(map.convert("map", data1).get("t2").get("name"), "tttt");

        Map<String, String> data2 = mmap();
        assertEquals(map.validate("map", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(map.convert("map", data2), Collections.EMPTY_MAP);
    }

    @Test
    public void testMap_LongKey() {
        System.out.println(green(">> map - long key"));

        Mapping<Map<Long, String>> map = Mappings.map(Mappings.vLong(), Mappings.text());

        Map<String, String> data1 = mmap(
                entry("map.101", "ttt"),
                entry("map.102", "tet")
            );
        assertEquals(map.validate("map", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(map.convert("map", data1), mmap(
                entry(101l, "ttt"), entry(102l, "tet")));

        Map<String, String> data2 = mmap();
        assertEquals(map.validate("map", data2, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(map.convert("map", data2), Collections.EMPTY_MAP);
    }

}
