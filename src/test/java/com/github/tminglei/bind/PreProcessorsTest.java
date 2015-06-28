package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

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
    }

}
