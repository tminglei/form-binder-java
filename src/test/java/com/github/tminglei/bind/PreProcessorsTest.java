package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class PreProcessorsTest {

    @BeforeClass
    public void start() {
        System.out.println(cyan("test pre-defined pre-processors"));
    }

    // trim test

    @Test
    public void testTrim_SingleInput() {
        System.out.println(green(">> trim - single input"));

        PreProcessor trim = Processors.trim();

        assertEquals(trim.apply("", mmap(entry("", null)), Options.EMPTY),
                mmap(entry("", "")));
        assertEquals(trim.apply("", mmap(entry("", " yuu")), Options.EMPTY),
                mmap(entry("", "yuu")));
        assertEquals(trim.apply("a", mmap(entry("a", " eyuu ")), Options.EMPTY),
                mmap(entry("a", "eyuu")));
    }

    @Test
    public void testTrim_MultiInput() {
        System.out.println(green(">> trim - multiple input"));

        PreProcessor trim = Processors.trim();

        assertEquals(trim.apply("", mmap(entry("a", null), entry("b", " t1")), Options.EMPTY),
                mmap(entry("a", ""), entry("b", "t1")));
        assertEquals(trim.apply("t", mmap(entry("", " yuu"), entry("t.a", " ")), Options.EMPTY),
                mmap(entry("", " yuu"), entry("t.a", "")));
        assertEquals(trim.apply("a", mmap(entry("a[1]", " eyuu ")), Options.EMPTY),
                mmap(entry("a[1]", "eyuu")));
    }

    // omit test

    @Test
    public void testOmit_SingleInput() {
        System.out.println(green(">> omit - single input"));

        PreProcessor omit = Processors.omit(",");

        assertEquals(omit.apply("", mmap(entry("", null)), Options.EMPTY),
                mmap(entry("", "")));
        assertEquals(omit.apply("", mmap(entry("", "123,334")), Options.EMPTY),
                mmap(entry("", "123334")));
        assertEquals(omit.apply("a", mmap(entry("a", "2.345e+5")), Options.EMPTY),
                mmap(entry("a", "2.345e+5")));
    }

    @Test
    public void testOmit_MultiInput() {
        System.out.println(green(">> omit - multiple input"));

        PreProcessor omit = Processors.omit(",");

        assertEquals(omit.apply("", mmap(entry("", null), entry("b", "123,334")), Options.EMPTY),
                mmap(entry("", ""), entry("b", "123334")));
        assertEquals(omit.apply("a", mmap(entry("", "123,334"), entry("a[1]", "2.345e+5")), Options.EMPTY),
                mmap(entry("", "123,334"), entry("a[1]", "2.345e+5")));
    }

    // omit-left test

    @Test
    public void testOmitLeft_SingleInput() {
        System.out.println(green(">> omit left - single input"));

        PreProcessor omitLeft = Processors.omitLeft("$");

        assertEquals(omitLeft.apply("", mmap(entry("", null)), Options.EMPTY),
                mmap(entry("", "")));
        assertEquals(omitLeft.apply("a", mmap(entry("a", "$3,567")), Options.EMPTY),
                mmap(entry("a", "3,567")));
    }

    @Test
    public void testOmitLeft_MultiInput() {
        System.out.println(green(">> omit left - multiple input"));

        PreProcessor omitLeft = Processors.omitLeft("$");

        assertEquals(omitLeft.apply("a", mmap(entry("", "$3,567"), entry("a", "$356")), Options.EMPTY),
                mmap(entry("", "$3,567"), entry("a", "356")));
        assertEquals(omitLeft.apply("a", mmap(entry("a[1]", "$3,567"), entry("a[2]", "$356")), Options.EMPTY),
                mmap(entry("a[1]", "3,567"), entry("a[2]", "356")));
    }

    // omit-right test

    @Test
    public void testOmitRight_SingleInput() {
        System.out.println(green(">> omit right - single input"));

        PreProcessor omitRight = Processors.omitRight("-tat");

        assertEquals(omitRight.apply("", mmap(entry("", null)), Options.EMPTY),
                mmap(entry("", "")));
        assertEquals(omitRight.apply("a", mmap(entry("a", "tewwwtt-tat")), Options.EMPTY),
                mmap(entry("a", "tewwwtt")));
    }

    @Test
    public void testOmitRight_MultiInput() {
        System.out.println(green(">> omit right - multiple input"));

        PreProcessor omitRight = Processors.omitRight("-tat");

        assertEquals(omitRight.apply("a", mmap(entry("", "tewwwtt-tat"), entry("a", "tew-tat")), Options.EMPTY),
                mmap(entry("", "tewwwtt-tat"), entry("a", "tew")));
        assertEquals(omitRight.apply("a", mmap(entry("a[0]", "tewwwtt-tat"), entry("a[1]", "tew-tat")), Options.EMPTY),
                mmap(entry("a[0]", "tewwwtt"), entry("a[1]", "tew")));
    }

    // omit-redundant test

    @Test
    public void testOmitRedundant_SingleInput() {
        System.out.println(green(">> omit redundant - single input"));

        PreProcessor omitRedundant = Processors.omitRedundant(" ");

        assertEquals(omitRedundant.apply("", mmap(entry("", null)), Options.EMPTY),
                mmap(entry("", "")));
        assertEquals(omitRedundant.apply("a", mmap(entry("a", " a  teee  86y")), Options.EMPTY),
                mmap(entry("a", " a teee 86y")));
    }

    @Test
    public void testOmitRedundant_MultiInput() {
        System.out.println(green(">> omit redundant - multiple input"));

        PreProcessor omitRedundant = Processors.omitRedundant(" ");

        assertEquals(omitRedundant.apply("a", mmap(entry("", " a  teee  86y"), entry("a", " a  teee  86")), Options.EMPTY),
                mmap(entry("", " a  teee  86y"), entry("a", " a teee 86")));
        assertEquals(omitRedundant.apply("a", mmap(entry("a[0]", " a  teee  86y"), entry("a[1]", " a  teee  86")), Options.EMPTY),
                mmap(entry("a[0]", " a teee 86y"), entry("a[1]", " a teee 86")));
    }

    // omit-matched test

    @Test
    public void testOmitMatched_SingleInput() {
        System.out.println(green(">> omit matched - single input"));

        PreProcessor omitMatched = Processors.omitMatched("-\\d\\d$");

        assertEquals(omitMatched.apply("", mmap(entry("", null)), Options.EMPTY),
                mmap(entry("", "")));
        assertEquals(omitMatched.apply("", mmap(entry("", "2342-334-12")), Options.EMPTY),
                mmap(entry("", "2342-334")));
        assertEquals(omitMatched.apply("a", mmap(entry("a", "2342-334")), Options.EMPTY),
                mmap(entry("a", "2342-334")));
    }

    @Test
    public void testOmitMatched_MultiInput() {
        System.out.println(green(">> omit matched - multiple input"));

        PreProcessor omitMatched = Processors.omitMatched("-\\d\\d$");

        assertEquals(omitMatched.apply("", mmap(entry("", "2342-334-12"), entry("a", "2342-334-13")), Options.EMPTY),
                mmap(entry("", "2342-334"), entry("a", "2342-334")));
        assertEquals(omitMatched.apply("a", mmap(entry("", "2342-334-12"), entry("a", "2342-334")), Options.EMPTY),
                mmap(entry("", "2342-334-12"), entry("a", "2342-334")));
    }

    // replace-matched test

    @Test
    public void testReplaceMatched_SingleInput() {
        System.out.println(green(">> replace matched - single input"));

        PreProcessor replaceMatched = Processors.replaceMatched("-\\d\\d$", "-1");

        assertEquals(replaceMatched.apply("", mmap(entry("", null)), Options.EMPTY),
                mmap(entry("", "")));
        assertEquals(replaceMatched.apply("", mmap(entry("", "2342-334-12")), Options.EMPTY),
                mmap(entry("", "2342-334-1")));
        assertEquals(replaceMatched.apply("a", mmap(entry("a", "2342-334")), Options.EMPTY),
                mmap(entry("a", "2342-334")));
    }

    @Test
    public void testReplaceMatched_MultiInput() {
        System.out.println(green(">> replace matched - multiple input"));

        PreProcessor replaceMatched = Processors.replaceMatched("-\\d\\d$", "-1");

        assertEquals(replaceMatched.apply("", mmap(entry("", "2342-334-12"), entry("a", "2342-334-13")), Options.EMPTY),
                mmap(entry("", "2342-334-1"), entry("a", "2342-334-1")));
        assertEquals(replaceMatched.apply("a", mmap(entry("", "2342-334-12"), entry("a", "2342-334")), Options.EMPTY),
                mmap(entry("", "2342-334-12"), entry("a", "2342-334")));
    }

    // expand-json test

    @Test
    public void testExpandJson_DirectUse() {
        System.out.println(green(">> expand json - direct use"));

        PreProcessor expandJson = Processors.expandJson();

        Map<String, String> rawData = mmap(
                entry("aa", "wett"),
                entry("json", "{\"id\":123, \"name\":\"tewd\", \"dr-1\":[33,45]}")
            );
        Map<String, String> expected = mmap(
                entry("aa", "wett"),
                entry("json.id", "123"),
                entry("json.name", "tewd"),
                entry("json.dr-1[0]", "33"),
                entry("json.dr-1[1]", "45")
            );

        assertEquals(expandJson.apply("json", rawData, Options.EMPTY),
                expected);
    }

    @Test
    public void testExpandJson_NullOrEmpty() {
        System.out.println(green(">> expand json - null or empty"));

        PreProcessor expandJson = Processors.expandJson();

        Map<String, String> nullData = mmap(entry("aa", "wett"));
        try {
            assertEquals(expandJson.apply("json", nullData, Options.EMPTY),
                    nullData);
        } catch (NullPointerException e) {
            // expected
        }

        Map<String, String> nullData1 = mmap(entry("aa", "wett"), entry("json", null));
        try {
            assertEquals(expandJson.apply("json", nullData1, Options.EMPTY),
                    mmap(entry("aa", "wett")));
        } catch (NullPointerException e) {
            // expected
        }

        Map<String, String> emptyData1 = mmap(entry("aa", "wett"), entry("json", ""));
        try {
            assertEquals(expandJson.apply("json", emptyData1, Options.EMPTY),
                    mmap(entry("aa", "wett")));
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testExpandJson_WithPrefix() {
        System.out.println(green(">> expand json - with prefix"));

        PreProcessor expandJson = Processors.expandJson("json");

        Map<String, String> rawData = mmap(
                entry("aa", "wett"),
                entry("json", "{\"id\":123, \"name\":\"tewd\", \"dr-1\":[33,45]}")
            );
        Map<String, String> expected = mmap(
                entry("aa", "wett"),
                entry("json.id", "123"),
                entry("json.name", "tewd"),
                entry("json.dr-1[0]", "33"),
                entry("json.dr-1[1]", "45")
            );

        assertEquals(expandJson.apply("", rawData, Options.EMPTY),
                expected);
    }

    // expand-list-keys test

    @Test
    public void testExpandListKeys_DirectUse() {
        System.out.println(green(">> expand list keys - direct use"));

        PreProcessor expandListKeys = Processors.expandListKeys();

        Map<String, String> rawData = mmap(
                entry("aa", "wett"),
                entry("touched[0]", "a[0]"),
                entry("touched[1]", "b"),
                entry("touched[2]", "c.t")
            );
        Map<String, String> expected = mmap(
                entry("aa", "wett"),
                entry("touched.a[0]", "true"),
                entry("touched.b", "true"),
                entry("touched.c.t", "true")
            );

        assertEquals(expandListKeys.apply("touched", rawData, Options.EMPTY),
                expected);
    }

    @Test
    public void testExpandListKeys_WithPrefix() {
        System.out.println(green(">> expand json keys - with prefix"));

        PreProcessor expandJsonKeys = Processors.expandJsonKeys("touched");

        Map<String, String> rawData = mmap(
                entry("aa", "wett"),
                entry("touched[0]", "a[0]"),
                entry("touched[1]", "b"),
                entry("touched[2]", "c.t")
            );
        Map<String, String> expected = mmap(
                entry("aa", "wett"),
                entry("touched.a[0]", "true"),
                entry("touched.b", "true"),
                entry("touched.c.t", "true")
            );

        assertEquals(expandJsonKeys.apply("", rawData, Options.EMPTY),
                expected);

    }

    // expand-json-keys test

    @Test
    public void testExpandJsonKeys_DirectUse() {
        System.out.println(green(">> expand json keys - direct use"));

        PreProcessor expandJsonKeys = Processors.expandJsonKeys();

        Map<String, String> rawData = mmap(
                entry("aa", "wett"),
                entry("touched", "[\"a[0]\",\"b\", \"c.t\"]")
            );
        Map<String, String> expected = mmap(
                entry("aa", "wett"),
                entry("touched.a[0]", "true"),
                entry("touched.b", "true"),
                entry("touched.c.t", "true")
            );

        assertEquals(expandJsonKeys.apply("touched", rawData, Options.EMPTY),
                expected);
    }

    @Test
    public void testExpandJsonKeys_WithPrefix() {
        System.out.println(green(">> expand json keys - with prefix"));

        PreProcessor expandJsonKeys = Processors.expandJsonKeys("touched");

        Map<String, String> rawData = mmap(
                entry("aa", "wett"),
                entry("touched", "[\"a[0]\",\"b\", \"c.t\"]")
            );
        Map<String, String> expected = mmap(
                entry("aa", "wett"),
                entry("touched.a[0]", "true"),
                entry("touched.b", "true"),
                entry("touched.c.t", "true")
            );

        assertEquals(expandJsonKeys.apply("", rawData, Options.EMPTY),
                expected);
    }

    // change-prefix test

    @Test
    public void testChangePrefix_DirectUse() {
        System.out.println(green(">> change prefix - direct use"));

        PreProcessor changePrefix = Processors.changePrefix("json", "data");

        Map<String, String> data = mmap(
                entry("aa", "wett"),
                entry("json.id", "123"),
                entry("json.name", "tewd"),
                entry("json.dr-1[0]", "33"),
                entry("json.dr-1[1]", "45")
            );
        Map<String, String> expected = mmap(
                entry("aa", "wett"),
                entry("data.id", "123"),
                entry("data.name", "tewd"),
                entry("data.dr-1[0]", "33"),
                entry("data.dr-1[1]", "45")
            );

        assertEquals(changePrefix.apply("", data, Options.EMPTY),
                expected);
    }

    @Test
    public void testChangePrefix_WithHeadDotOmit() {
        System.out.println(green(">> change prefix - with head dot omitting"));

        PreProcessor changePrefix = Processors.changePrefix("json", "");

        Map<String, String> data = mmap(
                entry("aa", "wett"),
                entry("json.id", "123"),
                entry("json.name", "tewd"),
                entry("json.dr-1[0]", "33"),
                entry("json.dr-1[1]", "45")
            );
        Map<String, String> expected = mmap(
                entry("aa", "wett"),
                entry("id", "123"),
                entry("name", "tewd"),
                entry("dr-1[0]", "33"),
                entry("dr-1[1]", "45")
            );

        assertEquals(changePrefix.apply("", data, Options.EMPTY),
                expected);
    }

}
