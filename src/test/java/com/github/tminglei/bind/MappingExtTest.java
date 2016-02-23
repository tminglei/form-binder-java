package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.tminglei.bind.spi.*;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Mappings.*;
import static com.github.tminglei.bind.Utils.*;

public class MappingExtTest {

    @BeforeClass
    public void start() {
        System.out.println(cyan("test extension mechanism"));
    }

    @Test
    public void testExt() {
        System.out.println(green(">> ext setting/checking"));

        GroupMapping mapping = mapping(
                field("id", vLong().$ext(e -> ext(e).in("path").desc("pet id"))),
                field("name", text().$ext(e -> ext(e).in("query").desc("pet name")))
            );

        Extensible idExt = mapping.fields().get(0).getValue().options()._extData();
        Extensible nameExt = mapping.fields().get(1).getValue().options()._extData();

        assertEquals(idExt, new Ext().in("path").desc("pet id"));
        assertEquals(nameExt, new Ext().in("query").desc("pet name"));
    }
}
