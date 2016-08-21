package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Mappings.*;
import static com.github.tminglei.bind.Constraints.*;
import static com.github.tminglei.bind.Processors.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class GroupMappingsTest {
    private ResourceBundle bundle = ResourceBundle.getBundle("bind-messages");
    private Messages messages = (key) -> bundle.getString(key);

    private Mapping<BindObject> mapping1 = mapping(
            field("count", intv())
        ).label("xx")
            .verifying((label, vObj, messages1) -> {
                int count = vObj.get("count");
                if (count < 3) return Arrays.asList("count cannot less than 3");
                else if (count > 10) return Arrays.asList("count cannot greater then 10");
                else return Collections.EMPTY_LIST;
            });
    private Mapping<BindObject> mapping2 = mapping(
            field("price", floatv()),
            field("count", intv().verifying(min(3), max(10)))
        ).label("xx")
            .verifying((label, vObj, messages1) -> {
                float price = vObj.get("price");
                int count = vObj.get("count");
                if (price * count > 1000) return Arrays.asList("total cost too much!");
                else return Collections.EMPTY_LIST;
            });
    private Mapping<BindObject> mappingx = mapping(
            field("email", text(maxLength(20, "%s: length > %s"), email("%s: invalid email"), required("%s is required"))),
            field("count", intv().verifying(max(10, "%s > %s"), max(15, "%s > %s")))
        );

    @BeforeClass
    public void start() {
        System.out.println(cyan("test pre-defined group mapping"));
    }

    // group mapping 1 test

    @Test
    public void testGroupMapping1_InvalidData() {
        System.out.println(green(">> group mapping 1 - invalid data"));

        Map<String, String> data = newmap(entry("count", "t5"));

        assertEquals(mapping1.validate("", data, messages, Options.EMPTY),
                Arrays.asList(entry("count", "'t5' must be a number")));
    }

    @Test
    public void testGroupMapping1_ValidData() {
        System.out.println(green(">> group mapping 1 - valid data"));

        Map<String, String> data = newmap(entry("count", "5"));

        assertEquals(mapping1.validate("", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping1.convert("", data).get("count"), Integer.valueOf(5));
    }

    @Test
    public void testGroupMapping1_OutOfScopeData() {
        System.out.println(green(">> group mapping 1 - out of scope data"));

        Map<String, String> data = newmap(entry("count", "15"));

        assertEquals(mapping1.validate("", data, messages, Options.EMPTY),
                Arrays.asList(entry("", "count cannot greater then 10")));
    }

    @Test
    public void testGroupMapping1_NullData() {
        System.out.println(green(">> group mapping 1 - null data"));

        Map<String, String> data = newmap();

        assertEquals(mapping1.validate("", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping1.convert("", data), null);
    }

    @Test
    public void testGroupMapping1_EmptyData() {
        System.out.println(green(">> group mapping 1 - empty data"));

        Map<String, String> data = newmap(entry("", ""));

        assertEquals(mapping1.validate("", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping1.convert("", data), null);
    }

    // group mapping 2 test

    @Test
    public void testGroupMapping2_InvalidData() {
        System.out.println(green(">> group mapping 2 - invalid data"));

        Map<String, String> data = newmap(entry("price", "23.5f"), entry("count", "t5"));

        assertEquals(mapping2.validate("", data, messages, Options.EMPTY),
                Arrays.asList(entry("count", "'t5' must be a number")));
    }

    @Test
    public void testGroupMapping2_ValidData() {
        System.out.println(green(">> group mapping 2 - valid data"));

        Map<String, String> data = newmap(entry("price", "23.5f"), entry("count", "5"));

        assertEquals(mapping2.validate("", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping2.convert("", data).get("price"), Float.valueOf(23.5f));
        assertEquals(mapping2.convert("", data).get("count"), Integer.valueOf(5));
    }

    @Test
    public void testGroupMapping2_OutOfScopeData1() {
        System.out.println(green(">> group mapping 2 - out of scope data 1"));

        Map<String, String> data = newmap(entry("price", "23.5f"), entry("count", "15"));

        assertEquals(mapping2.validate("", data, messages, Options.EMPTY),
                Arrays.asList(entry("count", "'15' must be lower than 10 (include boundary: true)")));
    }

    @Test
    public void testGroupMapping2_OutOfScopeData2() {
        System.out.println(green(">> group mapping 2 - out of scope data 2"));

        Map<String, String> data = newmap(entry("price", "123.5f"), entry("count", "9"));

        assertEquals(mapping2.validate("", data, messages, Options.EMPTY),
                Arrays.asList(entry("", "total cost too much!")));
    }

    @Test
    public void testGroupMapping2_NullData() {
        System.out.println(green(">> group mapping 2 - null data"));

        Map<String, String> data = newmap();

        assertEquals(mapping2.validate("", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping2.convert("", data), null);
    }

    @Test
    public void testGroupMapping2_EmptyData() {
        System.out.println(green(">> group mapping 2 - empty data"));

        Map<String, String> data = newmap(entry("count", "9"));

        assertEquals(mapping2.validate("", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping2.convert("", data).get("price"), Float.valueOf(0.0f));
        assertEquals(mapping2.convert("", data).get("count"), Integer.valueOf(9));
    }

    // group mapping with options test

    @Test
    public void testGroupMapping_WithEagerCheck() {
        System.out.println(green(">> group mapping - with eager check"));

        Mapping<BindObject> mapping = mappingx.options(o -> o.eagerCheck(true));
        Map<String, String> data = newmap(
                entry("email", "etttt.att#example-1111111.com"),
                entry("count", "20")
        );

        assertEquals(mapping.validate("", data, messages, Options.EMPTY),
                Arrays.asList(
                    entry("email", "etttt.att#example-1111111.com: length > 20"),
                    entry("email", "etttt.att#example-1111111.com: invalid email"),
                    entry("count", "20 > 10"),
                    entry("count", "20 > 15")
                ));
    }

    @Test
    public void testGroupMapping_WithIgnoreEmpty() {
        System.out.println(green(">> group mapping - with ignore empty"));

        Map<String, String> nullData = newmap();
        Map<String, String> emptyData = newmap(entry("count", ""));

        assertEquals(mappingx.validate("", nullData, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mappingx.options(o -> o.skipUntouched(true))
                        .validate("", nullData, messages, Options.EMPTY),
                Collections.EMPTY_LIST);

        assertEquals(mappingx.validate("", emptyData, messages, Options.EMPTY),
                Arrays.asList(entry("email", "email is required")));
        assertEquals(mappingx.options(o -> o.skipUntouched(true))
                        .validate("", emptyData, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
    }

    @Test
    public void testGroupMapping_WithIgnoreEmptyAndTouched() {
        System.out.println(green(">> group mapping - with ignore empty and touched"));

        Mapping<BindObject> mapping = mappingx.options(o -> o.skipUntouched(true));
        Map<String, String> data = newmap(entry("count", ""));

        assertEquals(mapping.validate("", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mapping.validate("", data, messages, Options.EMPTY
                        .touchedChecker(listTouched(Arrays.asList("email")))),
                Arrays.asList(entry("email", "email is required")));
    }

}
