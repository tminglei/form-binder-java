package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Utils.*;

public class ExtraConstraintsTest {
    Messages dummyMessages = (key) -> "dummy";

    @BeforeClass
    public void start() {
        System.out.println(cyan("test pre-defined extra constraints"));
    }

    // min test

    @Test
    public void testMin_Int() {
        System.out.println(green(">> min - for int, with custom message"));

        ExtraConstraint<Integer> min = Constraints.min(5, "%s cannot < %s");

        assertEquals(min.apply("xx", 6, dummyMessages),
                Collections.EMPTY_LIST);
        assertEquals(min.apply("xx", 3, dummyMessages),
                Arrays.asList("xx cannot < 5"));
    }

    @Test
    public void testMin_Double() {
        System.out.println(green(">> min - for double, with custom message"));

        ExtraConstraint<Double> min = Constraints.min(5.5d, "%s cannot < %s");

        assertEquals(min.apply("xx", 6d, dummyMessages),
                Collections.EMPTY_LIST);
        assertEquals(min.apply("xx", 3d, dummyMessages),
                Arrays.asList("xx cannot < 5.5"));
    }

    // max test

    @Test
    public void testMax_Int() {
        System.out.println(green(">> max - for int, with custom message"));

        ExtraConstraint<Integer> max = Constraints.max(15, "%s cannot > %s");

        assertEquals(max.apply("xx", 6, dummyMessages),
                Collections.EMPTY_LIST);
        assertEquals(max.apply("xx", 23, dummyMessages),
                Arrays.asList("xx cannot > 15"));
    }

    @Test
    public void testMax_Calendar() {
        System.out.println(green(">> max - for calendar, with custom message"));

        Calendar c = Calendar.getInstance();
        ExtraConstraint<Calendar> max = Constraints.max(c, "%s cannot > %s");

        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.MONTH, -1);
        assertEquals(max.apply("xx", c1, dummyMessages),
                Collections.EMPTY_LIST);

        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.MONTH, 1);
        assertEquals(max.apply("xx", c2, dummyMessages),
                Arrays.asList("xx cannot > " + c.toString()));
    }

}
