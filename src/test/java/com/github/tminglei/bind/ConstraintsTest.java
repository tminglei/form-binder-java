package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.github.tminglei.bind.api.*;
import com.github.tminglei.bind.spi.*;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class ConstraintsTest {
    private ResourceBundle bundle = ResourceBundle.getBundle("bind-messages");
    private Messages messages = (key) -> bundle.getString(key);

    @BeforeClass
    public void start() {
        System.out.println(cyan("test pre-defined constraints"));
    }

    // required test

    @Test
    public void testRequired_SingleInput() {
        System.out.println(green(">> required - single input"));

        Constraint required = Constraints.required();

        assertEquals(required.apply("", newmap(entry("", null)), messages, new Options()._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'' is required")));
        assertEquals(required.apply("", newmap(entry("", "")), messages, new Options()._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'' is required")));
        assertEquals(required.apply("", newmap(entry("", "test")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Collections.EMPTY_LIST);
    }

    @Test
    public void testRequired_MultiInput() {
        System.out.println(green(">> required - multiple inputs"));

        Constraint required = Constraints.required("%s is required");

        assertEquals(required.apply("tt", newmap(entry("tt.a", "tt")), messages, new Options()._label("haha")._inputMode(InputMode.MULTIPLE)),
                Collections.EMPTY_LIST);
        assertEquals(required.apply("tt", newmap(entry("tt.a", null)), messages, new Options()._label("haha")._inputMode(InputMode.MULTIPLE)),
                Collections.EMPTY_LIST);
        assertEquals(required.apply("tt", newmap(entry("tt", null)), messages, new Options()._inputMode(InputMode.MULTIPLE)),
                Arrays.asList(entry("tt", "tt is required")));
        assertEquals(required.apply("tt", newmap(), messages, new Options()._inputMode(InputMode.MULTIPLE)),
                Arrays.asList(entry("tt", "tt is required")));
    }

    @Test
    public void testRequired_PloyInput() {
        System.out.println(green(">> required - polymorphic input"));

        Constraint required = Constraints.required("%s is required");

        assertEquals(required.apply("tt", newmap(entry("tt.a", "tt")), messages, new Options()._label("haha")._inputMode(InputMode.POLYMORPHIC)),
                Collections.EMPTY_LIST);
        assertEquals(required.apply("tt", newmap(entry("tt.a", null)), messages, new Options()._label("haha")._inputMode(InputMode.POLYMORPHIC)),
                Collections.EMPTY_LIST);
        assertEquals(required.apply("tt", newmap(entry("tt", null)), messages, new Options()._inputMode(InputMode.POLYMORPHIC)),
                Arrays.asList(entry("tt", "tt is required")));
        assertEquals(required.apply("tt.a", newmap(entry("tt.a", null)), messages, new Options()._inputMode(InputMode.POLYMORPHIC)),
                Arrays.asList(entry("tt.a", "a is required")));
    }

    // max length test

    @Test
    public void testMaxLength_SimpleUse() {
        System.out.println(green(">> max length - simple use"));

        Constraint maxlength = Constraints.maxLength(10);

        assertEquals(maxlength.apply("", newmap(entry("", "wetyyuu")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Collections.EMPTY_LIST);
        assertEquals(maxlength.apply("", newmap(entry("", "wetyettyiiie")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'wetyettyiiie' must be shorter than 10 characters (include boundary: true)")));
        assertEquals(maxlength.apply("", newmap(entry("", "tuewerri97")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Collections.EMPTY_LIST);
    }

    @Test
    public void testMaxLength_WithCustomMessage() {
        System.out.println(green(">> max length - with custom message"));

        Constraint maxlength = Constraints.maxLength(10, "'%s': length > %d");

        assertEquals(maxlength.apply("", newmap(entry("", "eewryuooerjhy")), messages, new Options()._label("haha")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'eewryuooerjhy': length > 10")));
    }

    // min length test

    @Test
    public void testMinLength_SimpleUse() {
        System.out.println(green(">> min length - simple use"));

        Constraint minlength = Constraints.minLength(3);

        assertEquals(minlength.apply("", newmap(entry("", "er")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'er' must be longer than 3 characters (include boundary: true)")));
        assertEquals(minlength.apply("", newmap(entry("", "ert6")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Collections.EMPTY_LIST);
        assertEquals(minlength.apply("", newmap(entry("", "tee")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Collections.EMPTY_LIST);
    }

    @Test
    public void testMinLength_WithoutBoundary() {
        System.out.println(green(">> min length - w/o boundary"));

        Constraint minlength = Constraints.minLength(3, false);

        assertEquals(minlength.apply("", newmap(entry("", "er")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'er' must be longer than 3 characters (include boundary: false)")));
        assertEquals(minlength.apply("", newmap(entry("", "ert6")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Collections.EMPTY_LIST);
        assertEquals(minlength.apply("", newmap(entry("", "tee")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'tee' must be longer than 3 characters (include boundary: false)")));
    }

    @Test
    public void testMinLength_WithCustomMessage() {
        System.out.println(green(">> min length - custom message"));

        Constraint minlength = Constraints.minLength(3, "'%s': length cannot < %d");

        assertEquals(minlength.apply("", newmap(entry("", "te")), messages, new Options()._label("haha")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'te': length cannot < 3")));
    }

    // length test

    @Test
    public void testLength_SimpleUse() {
        System.out.println(green(">> length - simple use"));

        Constraint length = Constraints.length(9);

        assertEquals(length.apply("", newmap(entry("", "123456789")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Collections.EMPTY_LIST);
        assertEquals(length.apply("", newmap(entry("", "123")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'123' must be 9 characters")));
        assertEquals(length.apply("", newmap(entry("", "1234567890")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'1234567890' must be 9 characters")));
    }

    @Test
    public void testLength_WithCustomMessage() {
        System.out.println(green(">> length - with custom message"));

        Constraint length = Constraints.length(9, "'%s': length not equals to %d");

        assertEquals(length.apply("", newmap(entry("", "123")), messages, new Options()._label("haha")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'123': length not equals to 9")));
    }

    // oneOf test

    @Test
    public void testOneOf_SimpleUse() {
        System.out.println(green(">> one of - simple use"));

        Constraint oneof = Constraints.oneOf(Arrays.asList("a", "b", "c"));

        assertEquals(oneof.apply("", newmap(entry("", "a")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Collections.EMPTY_LIST);
        assertEquals(oneof.apply("", newmap(entry("", "t")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'t' must be one of [a, b, c]")));
        assertEquals(oneof.apply("", newmap(entry("", null)), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'null' must be one of [a, b, c]")));
    }

    @Test
    public void testOneOf_WithCustomMessage() {
        System.out.println(green(">> one of - with custom message"));

        Constraint oneof = Constraints.oneOf(Arrays.asList("a", "b", "c"), "'%s': is not one of %s");

        assertEquals(oneof.apply("t.a", newmap(entry("t.a", "ts")), messages, new Options()._label("haha")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("t.a", "'ts': is not one of [a, b, c]")));
    }

    // pattern test

    @Test
    public void testPattern_SimpleUse() {
        System.out.println(green(">> pattern - simple use"));

        Constraint pattern = Constraints.pattern("^(\\d+)$");

        assertEquals(pattern.apply("", newmap(entry("", "1234657")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Collections.EMPTY_LIST);
        assertEquals(pattern.apply("", newmap(entry("", "32566y")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'32566y' must be '^(\\d+)$'")));
        assertEquals(pattern.apply("", newmap(entry("", "123,567")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'123,567' must be '^(\\d+)$'")));
    }

    @Test
    public void testPattern_WithCustomMessage() {
        System.out.println(green(">> pattern - with custom message"));

        Constraint pattern = Constraints.pattern("^(\\d+)$", "'%s' not match '%s'");

        assertEquals(pattern.apply("", newmap(entry("", "t4366")), messages, new Options()._label("haha")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'t4366' not match '^(\\d+)$'")));
    }

    // patternNot test

    @Test
    public void testPatternNot_SimpleUse() {
        System.out.println(green(">> pattern not - simple use"));

        Constraint patternNot = Constraints.patternNot(".*\\[(\\d*[^\\d\\[\\]]+\\d*)+\\].*");

        assertEquals(patternNot.apply("", newmap(entry("", "eree.[1234657].eee")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Collections.EMPTY_LIST);
        assertEquals(patternNot.apply("", newmap(entry("", "errr.[32566y].ereee")), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'errr.[32566y].ereee' mustn't be '.*\\[(\\d*[^\\d\\[\\]]+\\d*)+\\].*'")));
    }

    @Test
    public void testPatternNot_WithCustomMessage() {
        System.out.println(green(">> pattern not - with custom message"));

        Constraint patternNot = Constraints.pattern("^(\\d+)$", "'%s' contains illegal array index");

        assertEquals(patternNot.apply("", newmap(entry("", "ewtr.[t4366].eweee")), messages, new Options()._label("haha")._inputMode(InputMode.SINGLE)),
                Arrays.asList(entry("", "'ewtr.[t4366].eweee' contains illegal array index")));
    }

    // email test

    /**
     * test cases copied from:
     * http://en.wikipedia.org/wiki/Email_address
     */

    @Test
    public void testEmail_ValidEmailAddresses() {
        System.out.println(green(">> email - valid email addresses"));

        Constraint email = Constraints.email("'%s' not valid");

        Arrays.asList(
                "niceandsimple@example.com",
                "very.common@example.com",
                "a.little.lengthy.but.fine@dept.example.com",
                "disposable.style.email.with+symbol@example.com",
                "other.email-with-dash@example.com"//,
                //        "user@localserver",
                // internationalization examples
                //        "Pelé@example.com",  //Latin Alphabet (with diacritics)
                //        "δοκιμή@παράδειγμα.δοκιμή", //Greek Alphabet
                //        "我買@屋企.香港",  //Traditional Chinese Characters
                //        "甲斐@黒川.日本",  //Japanese Characters
                //        "чебурашка@ящик-с-апельсинами.рф"  //Cyrillic Characters
        ).stream().forEach(emailAddr -> {
            assertEquals(email.apply("", newmap(entry("", emailAddr)), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                    Collections.EMPTY_LIST);
        });
    }

    @Test
    public void testEmail_InvalidEmailAddresses() {
        System.out.println(green(">> email - invalid email addresses"));

        Constraint email = Constraints.email();

        Arrays.asList(
                "Abc.example.com", //(an @ character must separate the local and domain parts)
                "A@b@c@example.com", //(only one @ is allowed outside quotation marks)
                "a\"b(c)d,e:f;g<h>i[j\\k]l@example.com", //(none of the special characters in this local part is allowed outside quotation marks)
                "just\"not\"right@example.com", //(quoted strings must be dot separated or the only element making up the local-part)
                "this is\"not\\allowed@example.com", //(spaces, quotes, and backslashes may only exist when within quoted strings and preceded by a backslash)
                "this\\ still\\\"not\\\\allowed@example.com", //(even if escaped (preceded by a backslash), spaces, quotes, and backslashes must still be contained by quotes)
                "john..doe@example.com", //(double dot before @)
                "john.doe@example..com" //(double dot after @)
        ).stream().forEach(emailAddr -> {
            assertEquals(email.apply("", newmap(entry("", emailAddr)), messages, new Options()._label("")._inputMode(InputMode.SINGLE)),
                    Arrays.asList(entry("", "'" + emailAddr + "' is not a valid email")));
        });
    }

    // indexInKeys test

    @Test
    public void testIndexInKey_SimpleUse() {
        System.out.println(green(">> index in key - simple use"));

        Constraint index = Constraints.indexInKeys();

        assertEquals(index.apply("a", newmap(entry("a[0]", "aaa")), messages, new Options()._inputMode(InputMode.MULTIPLE)),
                Collections.EMPTY_LIST);
        assertEquals(index.apply("a", newmap(entry("a[t0]", "aaa"), entry("a[3]", "tew")), messages, new Options()._label("")._inputMode(InputMode.MULTIPLE)),
                Arrays.asList(entry("a[t0]", "'a[t0]' contains illegal array index")));
        assertEquals(index.apply("a", newmap(entry("a[t1]", "aewr"), entry("a[t4]", "ewre")), messages,
                        new Options()._label("xx")._inputMode(InputMode.MULTIPLE)).stream().collect(Collectors.toSet()),
                Arrays.asList(entry("a[t1]", "'a[t1]' contains illegal array index"), entry("a[t4]", "'a[t4]' contains illegal array index"))
                        .stream().collect(Collectors.toSet()));
    }

    @Test
    public void testIndexInKey_WithCustomMessage() {
        System.out.println(green(">> index in key - with custom message"));

        Constraint index = Constraints.indexInKeys("illegal array index (%s)");

        assertEquals(index.apply("a", newmap(entry("a[0]", "aaa")), messages, new Options()._label("xx")._inputMode(InputMode.MULTIPLE)),
                Collections.EMPTY_LIST);
        assertEquals(index.apply("a", newmap(entry("a[t0]", "aaa"), entry("a[3]", "tew")), messages, new Options()._label("")._inputMode(InputMode.MULTIPLE)),
                Arrays.asList(entry("a[t0]", "illegal array index (a[t0])")));
        assertEquals(index.apply("", newmap(entry("a[t1]", "aewr"), entry("a[t4].er", "ewre")), messages,
                        new Options()._label("xx")._inputMode(InputMode.MULTIPLE)).stream().collect(Collectors.toSet()),
                Arrays.asList(entry("a[t1]", "illegal array index (a[t1])"), entry("a[t4].er", "illegal array index (a[t4].er)"))
                        .stream().collect(Collectors.toSet()));
    }

}
