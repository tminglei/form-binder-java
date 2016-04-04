package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Mappings.*;
import static com.github.tminglei.bind.Utils.*;

public class AttachmentTest {

    @BeforeClass
    public void start() {
        System.out.println(cyan("test extension mechanism"));
    }

    @Test
    public void testAttachment() {
        System.out.println(green(">> ext setting/checking"));

        GroupMapping mapping = mapping(
                field("id", $(vLong()).in("path").desc("pet id").$$),
                field("name", $(text()).in("query").desc("pet name").$$)
            );

        Attachment idExt = (Attachment) mapping.fields().get(0).getValue().options()._attachment();
        Attachment nameExt = (Attachment) mapping.fields().get(1).getValue().options()._attachment();

        assertEquals(idExt, new Attachment("path", "pet id"));
        assertEquals(nameExt, new Attachment("query", "pet name"));
    }

    ///////////////////////////////////////////////////////////////////

    public static <T> AttachmentBuilder $(Framework.Mapping<T> mapping) {
        return new AttachmentBuilder(mapping);
    }

    public static class AttachmentBuilder<T> {
        public final Framework.Mapping<T> $$;

        AttachmentBuilder(Framework.Mapping<T> mapping) {
            this.$$ = mapping;
        }

        public AttachmentBuilder<T> in(String in) {
            Attachment curr = getOrCreateAttachment($$);
            return new AttachmentBuilder($$.options(o -> o._attachment(new Attachment(in, curr.desc))));
        }

        public AttachmentBuilder<T> desc(String desc) {
            Attachment curr = getOrCreateAttachment($$);
            return new AttachmentBuilder($$.options(o -> o._attachment(new Attachment(curr.in, desc))));
        }

        ///---

        private Attachment getOrCreateAttachment(Framework.Mapping<T> mapping) {
            return mapping.options()._attachment() == null ? new Attachment(null, null)
                : (Attachment) mapping.options()._attachment();
        }
    }

    public static class Attachment {
        public final String in;
        public final String desc;

        Attachment(String in, String desc) {
            this.in = in == null ? "" : in;
            this.desc = desc == null ? "" : desc;
        }

        ///
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Attachment) {
                Attachment other = (Attachment) obj;
                return in.equals(other.in)
                    && desc.equals(other.desc);
            } else return false;
        }
    }
}
