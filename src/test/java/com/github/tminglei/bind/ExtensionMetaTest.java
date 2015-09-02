package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Utils.*;

public class ExtensionMetaTest {

    @BeforeClass
    public void start() {
        System.out.println(cyan("test extension meta"));
    }

    // for constraints

    @Test
    public void testRequiredConstraint() {
        System.out.println(green(">>> constraint - required"));

        Constraint required = Constraints.required();
        assertEquals(required.meta().name, "required");
        assertEquals(required.meta().desc, "required()");
        assertEquals(required.meta().params, Collections.EMPTY_LIST);
    }

    @Test
    public void testMaxLengthConstraint() {
        System.out.println(green(">>> constraint - maxLength"));

        Constraint maxLength = Constraints.maxLength(20);
        assertEquals(maxLength.meta().name, "maxLength");
        assertEquals(maxLength.meta().desc, "maxLength(20)");
        assertEquals(maxLength.meta().params, Arrays.asList(20));
    }

    @Test
    public void testOneOfConstraint() {
        System.out.println(green(">>> constraint - oneOf"));

        Constraint oneOf = Constraints.oneOf(Arrays.asList("a", "b", "c"));
        assertEquals(oneOf.meta().name, "oneOf");
        assertEquals(oneOf.meta().desc, "oneOf([a, b, c])");
        assertEquals(oneOf.meta().params, Arrays.asList(Arrays.asList("a", "b", "c")));
    }

    // for extra constraints

    @Test
    public void testMinExtraConstraint() {
        System.out.println(green(">>> extra constraint - min"));

        ExtraConstraint<Integer> min = Constraints.min(10);
        assertEquals(min.meta().name, "min");
        assertEquals(min.meta().desc, "min(10)");
        assertEquals(min.meta().params, Arrays.asList(10));
    }

    // for pre-processors

    @Test
    public void testOmitPreProcessor() {
        System.out.println(green(">>> pre-processor - omit"));

        PreProcessor omit = Processors.omit("$");
        assertEquals(omit.meta().name, "omit");
        assertEquals(omit.meta().desc, "omit($)");
        assertEquals(omit.meta().params, Arrays.asList("$"));
    }

    @Test
    public void testReplaceMatchedPreProcessor() {
        System.out.println(green(">>> pre-processor - replaceMatched"));

        PreProcessor omit = Processors.replaceMatched("^\\$", "");
        assertEquals(omit.meta().name, "replaceMatched");
        assertEquals(omit.meta().desc, "replace(matched '^\\$' with '')");
        assertEquals(omit.meta().params, Arrays.asList("^\\$", ""));
    }

}
