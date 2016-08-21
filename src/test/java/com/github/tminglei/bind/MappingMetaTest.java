package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Mappings.*;
import static com.github.tminglei.bind.Utils.*;

public class MappingMetaTest {

    @BeforeClass
    public void start() {
        System.out.println(cyan("test mapping meta"));
    }

    // simple mappings

    @Test
    public void testTextMapping() {
        System.out.println(green(">>> text mapping - string"));

        Framework.Mapping<?> text = text();
        assertEquals(text.meta().targetType,
                String.class);
    }

    // general mappings

    @Test
    public void testIgnoredMapping() {
        System.out.println(green(">>> ignored mapping - string"));

        Framework.Mapping<?> ignored = ignored("test");
        assertEquals(ignored.meta().targetType,
                String.class);
    }

    @Test
    public void testDefaultValMapping() {
        System.out.println(green(">>> ignored mapping - string"));

        Framework.Mapping<?> defaultVal = defaultv(text(), "test");
        assertEquals(defaultVal.meta().targetType,
                Optional.class);
        assertEquals(defaultVal.meta().baseMappings[0].meta().targetType,
                String.class);
    }

    @Test
    public void testListMapping_Simple() {
        System.out.println(green(">>> list mapping - simple"));

        Framework.Mapping<?> list = list(text());
        assertEquals(list.meta().targetType,
                List.class);
        assertEquals(list.meta().baseMappings[0].meta().targetType,
                String.class);
    }

    @Test
    public void testListMapping_Compound() {
        System.out.println(green(">>> list mapping - compound"));

        Framework.Mapping<?> list = list(mapping(
                field("id", longv()),
                field("name", text())
            ));
        assertEquals(list.meta().targetType,
                List.class);
        assertEquals(list.meta().baseMappings[0].meta().targetType,
                BindObject.class);

        GroupMapping base = (GroupMapping) list.meta().baseMappings[0];
        assertEquals(base.fields().get(0).getValue().meta().targetType,
                Long.class);
        assertEquals(base.fields().get(1).getValue().meta().targetType,
                String.class);
    }

    @Test
    public void testMapMapping() {
        System.out.println(green(">>> map mapping - <string, list<long>>"));

        Framework.Mapping<?> map = map(list(longv()));
        assertEquals(map.meta().targetType,
                Map.class);
        assertEquals(map.meta().baseMappings[0].meta().targetType,
                String.class);
        assertEquals(map.meta().baseMappings[1].meta().targetType,
                List.class);
        assertEquals(map.meta().baseMappings[1].meta().baseMappings[0].meta().targetType,
                Long.class);
    }

    // group mappings

    @Test
    public void testGroupMapping() {
        System.out.println(green(">>> group mapping - { long, string }"));

        GroupMapping group = mapping(
                field("id", longv()),
                field("name", text())
            );
        assertEquals(group.meta().targetType,
                BindObject.class);
        assertEquals(group.fields().get(0).getValue().meta().targetType,
                Long.class);
        assertEquals(group.fields().get(1).getValue().meta().targetType,
                String.class);
    }

}
