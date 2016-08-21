package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Constraints.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Mappings.*;
import static com.github.tminglei.bind.Processors.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Transformers.*;

public class TransformTest {
    private ResourceBundle bundle = ResourceBundle.getBundle("bind-messages");
    private Messages messages = (key) -> "xx".equals(key) ? "haha" : bundle.getString(key);

    private Mapping<?> mapping =
            mapping(
                field("id", longv()),
                field("data", attach(expandJson()).to(mapping(
                    field("email", attach(required("%s is required")).to(text(maxLength(20, "%s: length > %s"), email("%s: invalid email")))),
                    field("price", attach(omitLeft("$")).to(floatv())),
                    field("count", intv().verifying(min(3), max(10)))
                )).map(transTo(Bean1.class)).label("xx").verifying((label, bean1, messages1) -> {
                    if (bean1.getPrice() * bean1.getCount() > 1000) {
                        return Arrays.asList(label + ": total cost too much!");
                    } else return Collections.EMPTY_LIST;
                }))
            ).map(transTo(Bean2.class));

    @BeforeClass
    public void start() {
        System.out.println(Utils.cyan("test bean transform"));
    }

    @Test
    public void testTransform() {
        Map<String, String> data = newmap(
                entry("id", "133"),
                entry("data", "{\"email\":\"etttt@example.com\", \"price\":\"$137.5\", \"count\":5}")
        );

        BindObject bindObj = new FormBinder(messages).bind(mapping, data);

        Bean2 expected = new Bean2(133, new Bean1("etttt@example.com", 137.5f, 5));

        assertEquals(bindObj.get(), expected);
    }

    ///---

    static class Bean1 {
        private String email;
        private float  price;
        private int    count;

        public Bean1() {}

        public Bean1(String email, float price, int count) {
            this.email = email;
            this.price = price;
            this.count = count;
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }

        public float getPrice() {
            return price;
        }
        public void setPrice(float price) {
            this.price = price;
        }

        public int getCount() {
            return count;
        }
        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public int hashCode() {
            int emailHash = email == null ? 0 : email.hashCode();
            return emailHash * 37 + Float.hashCode(price) * 17 + count;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Bean1) {
                Bean1 other = (Bean1) object;
                return price == other.price && count == other.count &&
                        (email == null ? other.email == null : email.equals(other.email));
            } else return false;
        }

        @Override
        public String toString() {
            StringBuilder bf = new StringBuilder();
            bf.append("{");
            bf.append("email=").append(email);
            bf.append(", ");
            bf.append("price=").append(price);
            bf.append(", ");
            bf.append("count=").append(count);
            bf.append("}");
            return bf.toString();
        }
    }

    static class Bean2 {
        private long  id;
        private Bean1 data;

        public Bean2() {}

        public Bean2(long id, Bean1 data) {
            this.id = id;
            this.data = data;
        }

        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }

        public Bean1 getData() {
            return data;
        }
        public void setData(Bean1 data) {
            this.data = data;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(id) * 17 + (data == null ? 0 : data.hashCode());
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Bean2) {
                Bean2 other = (Bean2) object;
                return id == other.id &&
                        (data == null ? other.data == null : data.equals(other.data));
            } else return false;
        }

        @Override
        public String toString() {
            StringBuilder bf = new StringBuilder();
            bf.append("{");
            bf.append("id=").append(id);
            bf.append(", ");
            bf.append("data=").append(data);
            bf.append("}");
            return bf.toString();
        }
    }
}
