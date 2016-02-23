package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Mappings.*;
import static com.github.tminglei.bind.Constraints.*;
import static com.github.tminglei.bind.Processors.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class FormBinderTest {
    private ResourceBundle bundle = ResourceBundle.getBundle("bind-messages");
    private Messages messages = (key) -> "xx".equals(key) ? "haha" : bundle.getString(key);

    private Mapping<BindObject> mapping =
        mapping(
            field("id", vLong()),
            field("data", attach(expandJson()).to(mapping(
                field("email", attach(required("%s is required")).to(text(maxLength(20, "%s: length > %s"), email("%s: invalid email")))),
                field("price", attach(omitLeft("$")).to(vFloat())),
                field("count", vInt().verifying(min(3), max(10)))
            )).label("xx").verifying((label, vObj, messages1) -> {
                float price = vObj.get("price");
                int count = vObj.get("count");
                if (price * count > 1000) {
                    return Arrays.asList(label + ": total cost too much!");
                } else return Collections.EMPTY_LIST;
            }))
        );

    @BeforeClass
    public void start() {
        System.out.println(cyan("test form binder facade"));
    }

    // bind test

    @Test
    public void testBind_ValidData() {
        System.out.println(green(">> bind - valid data"));

        Map<String, String> data = newmap(
                entry("id", "133"),
                entry("data", "{\"email\":\"etttt@example.com\", \"price\":\"$137.5\", \"count\":5}")
        );

        BindObject bindObj = new FormBinder(messages).bind(mapping, data);

        assertEquals(bindObj.errors().isPresent(), false);
        assertEquals(bindObj.get("id"), Long.valueOf(133));
        assertEquals(bindObj.node("data").get("email"), "etttt@example.com");
        assertEquals(bindObj.node("data").get("price"), Float.valueOf(137.5f));
        assertEquals(bindObj.node("data").get("count"), Integer.valueOf(5));

        /// for each usage
        for(Map.Entry<String, Object> e1 : bindObj) {
            if (e1.getKey().equals("id"))
                assertEquals(e1.getValue(), Long.valueOf(133));
            if (e1.getKey().equals("data")) {
                for(Map.Entry<String, Object> e2 : (BindObject) e1.getValue()) {
                    if (e2.getKey().equals("email"))
                        assertEquals(e2.getValue(), "etttt@example.com");
                    if (e2.getKey().equals("price"))
                        assertEquals(e2.getValue(), Float.valueOf(137.5f));
                    if (e2.getKey().equals("count"))
                        assertEquals(e2.getValue(), Integer.valueOf(5));
                }
            }
        }
    }

    @Test
    public void testBind_WithTouched() {
        System.out.println(green(">> bind - ignore empty and touched"));

        Map<String, String> data = newmap(
                entry("id", "133"),
                entry("data", "{\"email\":null, \"price\":337.5, \"count\":5}"),
                entry("touched", "[\"email\", \"price\"]")
        );
        Mapping<BindObject> mappingx = mapping
                .options(o -> o.ignoreEmpty(true))
                .options(o -> o.touched(prefixTouched("data", "touched")))
                .processor(expandJsonKeys("touched"));

        BindObject bindObj = new FormBinder(messages).bind(mappingx, data);

        assertEquals(bindObj.errors().isPresent(), true);
        assertEquals(bindObj.errors().get(), Arrays.asList(
                entry("data.email", "email is required")));
    }

    @Test
    public void testBind_WithI18N() {
        System.out.println(green(">> bind - i18n and label"));

        Map<String, String> data = newmap(
                entry("id", "133"),
                entry("data", "{\"email\":\"example@123.com\", \"price\":337.5, \"count\":5}")
        );
        Mapping<BindObject> mappingx = mapping.options(o -> o.i18n(true));

        BindObject bindObj = new FormBinder(messages).bind(mappingx, data);

        assertEquals(bindObj.errors().isPresent(), true);
        assertEquals(bindObj.errors().get(), Arrays.asList(
                entry("data", "haha: total cost too much!")));
    }

    @Test
    public void testBind_WithRoot() {
        System.out.println(green(">> bind - with root"));

        Map<String, String> data = newmap(
                entry("id", "133"),
                entry("body", "{\"data\": {\"email\":null, \"price\":337.5, \"count\":5}, \"touched\": [\"email\", \"price\"]}")
        );
        Mapping<BindObject> mappingx = mapping
                .options(o -> o.ignoreEmpty(true))
                .options(o -> o.touched(prefixTouched("body.data", "body.touched")))
                .processor(expandJson("body"));

        BindObject bindObj = new FormBinder(messages).bind(mappingx, data, "body");

        assertEquals(bindObj.errors().isPresent(), true);
        assertEquals(bindObj.errors().get(), Arrays.asList(
                entry("body.data", "xx: total cost too much!")));
    }

    // validate test

    @Test
    public void testValidate_ValidData() {
        System.out.println(green(">> validate - valid data"));

        Map<String, String> data = newmap(
                entry("id", "133"),
                entry("data", "{\"email\":\"etttt@example.com\", \"price\":\"$137.5\", \"count\":5}")
        );

        Optional<List<Map.Entry<String, String>>> errors = new FormBinder(messages)
                .validate(mapping, data);
        assertEquals(errors.isPresent(), false);
    }

    @Test
    public void testValidate_InvalidData() {
        System.out.println(green(">> validate - invalid data"));

        Map<String, String> data = newmap(
                entry("id", "133"),
                entry("data", "{\"email\":null, \"price\":337.5, \"count\":5}"),
                entry("touched", "[\"email\", \"price\"]")
        );
        Mapping<BindObject> mappingx = mapping
                .options(o -> o.ignoreEmpty(true))
                .options(o -> o.touched(prefixTouched("data", "touched")))
                .processor(expandJsonKeys("touched"));

        Optional<List<Map.Entry<String, String>>> errors = new FormBinder(messages)
                .validate(mappingx, data);

        assertEquals(errors.isPresent(), true);
        assertEquals(errors.get(), Arrays.asList(
                entry("data.email", "email is required")));
    }

    @Test
    public void testValidate_WithErrProcessor() {
        System.out.println(green(">> validate - with error processor"));

        Map<String, String> data = newmap(
                entry("id", "133"),
                entry("data", "{\"email\":null, \"price\":\"$337.5\", \"count\":5}")
        );

        Optional<List<Map.Entry<String, String>>> errors =
                new FormBinder(messages, foldErrs())
                        .validate(mapping, data);

        assertEquals(errors.isPresent(), true);
        assertEquals(errors.get(), newmap(
                entry("data.email", Arrays.asList("email is required"))));
    }

}
