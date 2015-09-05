package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class ErrProcessorsTest {

    @BeforeClass
    public void start() {
        System.out.println(cyan("test pre-defined err-processors"));
    }

    @Test
    public void testFoldErrs() {
        System.out.println(green(">> fold errors"));

        List<Map.Entry<String, String>> errors = Arrays.asList(
                entry("a", "tttt"),
                entry("t[1]", "tewte"),
                entry("t[1]", "tewee"),
                entry("a", "ttt"),
                entry("c", "te")
            );
        Map<String, List<String>> foldedErrs = newmap(
                entry("a", Arrays.asList("tttt", "ttt")),
                entry("t[1]", Arrays.asList("tewte", "tewee")),
                entry("c", Arrays.asList("te"))
        );

        assertEquals(Processors.foldErrs().apply(errors), foldedErrs);
    }

    @Test
    public void testErrsTree() {
        System.out.println(green(">> errors tree"));

        List<Map.Entry<String, String>> errors = Arrays.asList(
                entry("a", "tttt"),
                entry("t[1]", "tewte"),
                entry("t[1]", "tewee"),
                entry("a", "ttt"),
                entry("a.x", "xx"),
                entry("c", "te")
            );
        Map<String, Object> errsTree = newmap(
                entry("a", newmap(
                        entry("_errors", Arrays.asList("tttt", "ttt")),
                        entry("x", newmap(
                                entry("_errors", Arrays.asList("xx"))
                        ))
                )),
                entry("t", newmap(
                        entry("1", newmap(
                                entry("_errors", Arrays.asList("tewte", "tewee"))
                        ))
                )),
                entry("c", newmap(
                        entry("_errors", Arrays.asList("te"))
                ))
        );

        assertEquals(Processors.errsTree().apply(errors), errsTree);
    }
}
