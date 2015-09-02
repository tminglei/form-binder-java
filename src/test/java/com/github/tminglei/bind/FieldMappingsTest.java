package com.github.tminglei.bind;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.*;
import static com.github.tminglei.bind.Simple.*;
import static com.github.tminglei.bind.Constraints.*;
import static com.github.tminglei.bind.Processors.*;
import static com.github.tminglei.bind.FrameworkUtils.*;
import static com.github.tminglei.bind.Utils.*;

public class FieldMappingsTest {
    private ResourceBundle bundle = ResourceBundle.getBundle("bind-messages");
    private Messages messages = (key) -> bundle.getString(key);

    @BeforeClass
    public void start() {
        System.out.println(cyan("test pre-defined field mappings"));
    }

    // text test

    @Test
    public void testText_ValidData() {
        System.out.println(green(">> text - valid data"));

        Mapping<String> text = attach(trim()).to(Mappings.text());
        Map<String, String> data = mmap(entry("text", "tett "));

        assertEquals(text.validate("text", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(text.convert("text", data), "tett");
    }

    @Test
    public void testText_NullData() {
        System.out.println(green(">> text - null data"));

        Mapping<String> text = attach(trim()).to(Mappings.text());
        Map<String, String> data = mmap();

        assertEquals(text.validate("text", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(text.convert("text", data), null);
    }

    @Test
    public void testText_EagerCheck() {
        System.out.println(green(">> text - eager check"));

        Mapping<String> text = Mappings.text(maxLength(20, "%s: length > %s"), email("%s: invalid email"));
        Map<String, String> data = mmap(entry("text", "etttt.att#example-1111111.com"));

        assertEquals(text.validate("text", data, messages, new Options().eagerCheck(true)).stream().collect(Collectors.toSet()),
                Arrays.asList(
                        entry("text", "etttt.att#example-1111111.com: length > 20"),
                        entry("text", "etttt.att#example-1111111.com: invalid email")
                ).stream().collect(Collectors.toSet()));
    }

    @Test
    public void testText_IgnoreEmpty() {
        System.out.println(green(">> text - ignore empty"));

        Map<String, String> data = mmap();

        Mapping<String> text1 = Mappings.text(required("%s is required"));
        assertEquals(text1.validate("text", data, messages, Options.EMPTY),
                Arrays.asList(entry("text", "text is required")));

        Mapping<String> text2 = Mappings.text(required("%s is required"))
                .options(o -> o.ignoreEmpty(true));
        assertEquals(text2.validate("text", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(text2.convert("text", data), null);
    }

    @Test
    public void testText_IgnoreEmptyWithTouched() {
        System.out.println(green(">> text - ignore empty and touched"));

        Map<String, String> data = mmap();

        Mapping<String> text1 = Mappings.text(required("%s is required"))
                .options(o -> o.ignoreEmpty(true));
        assertEquals(text1.validate("text", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(text1.convert("text", data), null);

        Mapping<String> text2 = Mappings.text(required("%s is required"))
                .options(o -> o.ignoreEmpty(true));
        assertEquals(text2.validate("text", data, messages, new Options()
                        .touched(listTouched(Arrays.asList("text")))),
                Arrays.asList(entry("text", "text is required")));
    }

    // boolean test

    @Test
    public void testBoolean_ValidData() {
        System.out.println(green(">> boolean - valid data"));

        Mapping<Boolean> bool = Mappings.vBoolean();
        Map<String, String> data = mmap(entry("boolean", "true"));

        assertEquals(bool.validate("boolean", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(bool.convert("boolean", data), Boolean.TRUE);
    }

    @Test
    public void testBoolean_InvalidData() {
        System.out.println(green(">> boolean - invalid data"));

        Mapping<Boolean> bool = Mappings.vBoolean();
        Map<String, String> data = mmap(entry("boolean", "teed"));

        assertEquals(bool.validate("boolean", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
    }

    @Test
    public void testBoolean_NullData() {
        System.out.println(green(">> boolean - null data"));

        Mapping<Boolean> bool = Mappings.vBoolean();
        Map<String, String> data = mmap();

        assertEquals(bool.validate("boolean", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(bool.convert("boolean", data), Boolean.FALSE);
    }

    @Test
    public void testBoolean_EmptyData() {
        System.out.println(green(">> boolean - empty data"));

        Mapping<Boolean> bool = Mappings.vBoolean();
        Map<String, String> data = mmap(entry("boolean", ""));

        assertEquals(bool.validate("boolean", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(bool.convert("boolean", data), Boolean.FALSE);
    }

    // int test

    @Test
    public void testInt_InvalidData() {
        System.out.println(green(">> int - invalid data"));

        Mapping<Integer> integer = Mappings.vInt().label("xx");
        Map<String, String> data = mmap(entry("int", "t12345"));

        assertEquals(integer.validate("int", data, messages, Options.EMPTY),
                Arrays.asList(entry("int", "'t12345' must be a number")));
    }

    @Test
    public void testInt_OutOfScopeData() {
        System.out.println(green(">> int - out-of-scope data"));

        Mapping<Integer> integer = attach(omit(",")).to(Mappings.vInt())
                .verifying(min(1000), max(10000));
        Map<String, String> data = mmap(entry("int", "345"));

        assertEquals(integer.validate("int", data, messages, Options.EMPTY),
                Arrays.asList(entry("int", "'345' cannot be lower than 1000")));
    }

    @Test
    public void testInt_WithLongNumber() {
        System.out.println(green(">> int - long number"));

        Mapping<Integer> integer = attach(omit(",")).to(Mappings.vInt());
        Map<String, String> data = mmap(entry("int", "146894532240"));

        assertEquals(integer.validate("int", data, messages, Options.EMPTY),
                Arrays.asList(entry("int", "'146894532240' must be a number")));
    }

    @Test
    public void testInt_ValidDataWithComma() {
        System.out.println(green(">> int - valid data with comma"));

        Mapping<Integer> integer = attach(omit(",")).to(Mappings.vInt())
                .verifying(min(1000), max(10000));
        Map<String, String> data = mmap(entry("int", "3,549"));

        assertEquals(integer.validate("int", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(integer.convert("int", data), Integer.valueOf(3549));
    }

    @Test
    public void testInt_NullData() {
        System.out.println(green(">> int - null data"));

        Mapping<Integer> integer = attach(omit(",")).to(Mappings.vInt());
        Map<String, String> data = mmap();

        assertEquals(integer.validate("int", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(integer.convert("int", data), Integer.valueOf(0));
    }

    @Test
    public void testInt_EmptyData() {
        System.out.println(green(">> int - empty data"));

        Mapping<Integer> integer = attach(omit(",")).to(Mappings.vInt())
                .verifying(min(1000), max(10000));
        Map<String, String> data = mmap(entry("int", ""));

        assertEquals(integer.validate("int", data, messages, Options.EMPTY),
                Arrays.asList(entry("int", "'0' cannot be lower than 1000")));
        assertEquals(integer.convert("int", data), Integer.valueOf(0));
    }

    // double test

    @Test
    public void testDouble_InvalidData() {
        System.out.println(green(">> double - invalid data"));

        Mapping<Double> mDouble = Mappings.vDouble().label("xx");
        Map<String, String> data = mmap(entry("double", "tesstt"));

        assertEquals(mDouble.validate("double", data, messages, Options.EMPTY),
                Arrays.asList(entry("double", "'tesstt' must be a number")));
    }

    @Test
    public void testDouble_ValidData() {
        System.out.println(green(">> double - valid data"));

        Mapping<Double> mDouble = Mappings.vDouble();
        Map<String, String> data = mmap(entry("double", "23545.2355"));

        assertEquals(mDouble.validate("double", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mDouble.convert("double", data), Double.valueOf(23545.2355d));
    }

    @Test
    public void testDouble_NullData() {
        System.out.println(green(">> double - null data"));

        Mapping<Double> mDouble = Mappings.vDouble();
        Map<String, String> data = mmap();

        assertEquals(mDouble.validate("double", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mDouble.convert("double", data), Double.valueOf(0d));
    }

    @Test
    public void testDouble_EmptyData() {
        System.out.println(green(">> double - empty data"));

        Mapping<Double> mDouble = Mappings.vDouble();
        Map<String, String> data = mmap(entry("double", ""));

        assertEquals(mDouble.validate("double", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mDouble.convert("double", data), Double.valueOf(0d));
    }

    // float test

    @Test
    public void testFloat_InvalidData() {
        System.out.println(green(">> float - invalid data"));

        Mapping<Float> mFloat = Mappings.vFloat();
        Map<String, String> data = mmap(entry("float", "tesstt"));

        assertEquals(mFloat.validate("float", data, messages, Options.EMPTY),
                Arrays.asList(entry("float", "'tesstt' must be a number")));
    }

    @Test
    public void testFloat_ValidData() {
        System.out.println(green(">> float - valid data"));

        Mapping<Float> mFloat = Mappings.vFloat();
        Map<String, String> data = mmap(entry("float", "23545.2355"));

        assertEquals(mFloat.validate("float", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mFloat.convert("float", data), Float.valueOf(23545.2355f));
    }

    @Test
    public void testFloat_NullData() {
        System.out.println(green(">> float - null data"));

        Mapping<Float> mFloat = Mappings.vFloat();
        Map<String, String> data = mmap();

        assertEquals(mFloat.validate("float", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mFloat.convert("float", data), Float.valueOf(0f));
    }

    @Test
    public void testFloat_EmptyData() {
        System.out.println(green(">> float - empty data"));

        Mapping<Float> mFloat = Mappings.vFloat();
        Map<String, String> data = mmap(entry("float", ""));

        assertEquals(mFloat.validate("float", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mFloat.convert("float", data), Float.valueOf(0f));
    }

    // long test

    @Test
    public void testLong_InvalidData() {
        System.out.println(green(">> long - invalid data"));

        Mapping<Long> mLong = Mappings.vLong();
        Map<String, String> data = mmap(entry("long", "tesstt"));

        assertEquals(mLong.validate("long", data, messages, Options.EMPTY),
                Arrays.asList(entry("long", "'tesstt' must be a number")));
    }

    @Test
    public void testLong_ValidData() {
        System.out.println(green(">> long - valid data"));

        Mapping<Long> mLong = Mappings.vLong();
        Map<String, String> data = mmap(entry("long", "235452355"));

        assertEquals(mLong.validate("long", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mLong.convert("long", data), Long.valueOf(235452355l));
    }

    @Test
    public void testLong_NullData() {
        System.out.println(green(">> long - null data"));

        Mapping<Long> mLong = Mappings.vLong();
        Map<String, String> data = mmap();

        assertEquals(mLong.validate("long", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mLong.convert("long", data), Long.valueOf(0l));
    }

    @Test
    public void testLong_EmptyData() {
        System.out.println(green(">> long - empty data"));

        Mapping<Long> mLong = Mappings.vLong();
        Map<String, String> data = mmap(entry("long", ""));

        assertEquals(mLong.validate("long", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(mLong.convert("long", data), Long.valueOf(0l));
    }

    // bigDecimal test

    @Test
    public void testBigDecimal_InvalidData() {
        System.out.println(green(">> big decimal - invalid data"));

        Mapping<BigDecimal> bigDecimal = Mappings.bigDecimal();
        Map<String, String> data = mmap(entry("bigDecimal", "tesstt"));

        assertEquals(bigDecimal.validate("bigDecimal", data, messages, Options.EMPTY),
                Arrays.asList(entry("bigDecimal", "'tesstt' must be a number")));
    }

    @Test
    public void testBigDecimal_ValidData() {
        System.out.println(green(">> big decimal - valid data"));

        Mapping<BigDecimal> bigDecimal = Mappings.bigDecimal();
        Map<String, String> data = mmap(entry("bigDecimal", "23545.2355"));

        assertEquals(bigDecimal.validate("bigDecimal", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(bigDecimal.convert("bigDecimal", data), new BigDecimal("23545.2355"));
    }

    @Test
    public void testBigDecimal_NullData() {
        System.out.println(green(">> big decimal - null data"));

        Mapping<BigDecimal> bigDecimal = Mappings.bigDecimal();
        Map<String, String> data = mmap();

        assertEquals(bigDecimal.validate("bigDecimal", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(bigDecimal.convert("bigDecimal", data), BigDecimal.ZERO);
    }

    @Test
    public void testBigDecimal_EmptyData() {
        System.out.println(green(">> big decimal - empty data"));

        Mapping<BigDecimal> bigDecimal = Mappings.bigDecimal();
        Map<String, String> data = mmap(entry("bigDecimal", ""));

        assertEquals(bigDecimal.validate("bigDecimal", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(bigDecimal.convert("bigDecimal", data), BigDecimal.ZERO);
    }

    // bigInteger test

    @Test
    public void testBigInt_InvalidData() {
        System.out.println(green(">> big integer - invalid data"));

        Mapping<BigInteger> bigInt = Mappings.bigInt();
        Map<String, String> data = mmap(entry("bigInt", "23545.2355"));

        assertEquals(bigInt.validate("bigInt", data, messages, Options.EMPTY),
                Arrays.asList(entry("bigInt", "'23545.2355' must be a number")));
    }

    @Test
    public void testBigInt_ValidData() {
        System.out.println(green(">> big integer - valid data"));

        Mapping<BigInteger> bigInt = Mappings.bigInt();
        Map<String, String> data = mmap(entry("bigInt", "235452355"));

        assertEquals(bigInt.validate("bigInt", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(bigInt.convert("bigInt", data), new BigInteger("235452355"));
    }

    @Test
    public void testBigInt_NullData() {
        System.out.println(green(">> big integer - null data"));

        Mapping<BigInteger> bigInt = Mappings.bigInt();
        Map<String, String> data = mmap();

        assertEquals(bigInt.validate("bigInt", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(bigInt.convert("bigInt", data), BigInteger.ZERO);
    }

    @Test
    public void testBigInt_EmptyData() {
        System.out.println(green(">> big integer - empty data"));

        Mapping<BigInteger> bigInt = Mappings.bigInt();
        Map<String, String> data = mmap(entry("bigInt", ""));

        assertEquals(bigInt.validate("bigInt", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(bigInt.convert("bigInt", data), BigInteger.ZERO);
    }

    // uuid test

    @Test
    public void testUUID_InvalidData() {
        System.out.println(green(">> uuid - invalid data"));

        Mapping<UUID> uuid = Mappings.uuid();
        Map<String, String> data = mmap(entry("uuid", "tesstt"));

        assertEquals(uuid.validate("uuid", data, messages, Options.EMPTY),
                Arrays.asList(entry("uuid", "'tesstt' is not a valid uuid")));
    }

    @Test
    public void testUUID_ValidData() {
        System.out.println(green(">> uuid - valid data"));

        UUID uuidObj = UUID.randomUUID();
        Mapping<UUID> uuid = Mappings.uuid();
        Map<String, String> data = mmap(entry("uuid", uuidObj.toString()));

        assertEquals(uuid.validate("uuid", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(uuid.convert("uuid", data), uuidObj);
    }

    @Test
    public void testUUID_NullData() {
        System.out.println(green(">> uuid - null data"));

        Mapping<UUID> uuid = Mappings.uuid();
        Map<String, String> data = mmap();

        assertEquals(uuid.validate("uuid", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(uuid.convert("uuid", data), null);
    }

    @Test
    public void testUUID_EmptyData() {
        System.out.println(green(">> uuid - empty data"));

        Mapping<UUID> uuid = Mappings.uuid();
        Map<String, String> data = mmap(entry("uuid", ""));

        assertEquals(uuid.validate("uuid", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(uuid.convert("uuid", data), null);
    }

    // date test

    @Test
    public void testDate_InvalidData() {
        System.out.println(green(">> date - invalid data"));

        Mapping<LocalDate> date = Mappings.date().label("xx");
        Map<String, String> data = mmap(entry("date", "5/3/2003"));

        assertEquals(date.validate("date", data, messages, Options.EMPTY),
                Arrays.asList(entry("date", "'xx' must satisfy any of following: " +
                        "['5/3/2003' not a date long, '5/3/2003' must be 'yyyy-MM-dd']")));
    }

    @Test
    public void testDate_ValidData() {
        System.out.println(green(">> date - valid data"));

        long timestamp = System.currentTimeMillis();
        LocalDate dateObj = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC")).toLocalDate();
        Mapping<LocalDate> date = Mappings.date();

        Map<String, String> data = mmap(entry("date", dateObj.toString()));
        assertEquals(date.validate("date", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(date.convert("date", data), dateObj);

        Map<String, String> data1 = mmap(entry("date", "" + timestamp));
        assertEquals(date.validate("date", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(date.convert("date", data1), dateObj);
    }

    @Test
    public void testDate_NullData() {
        System.out.println(green(">> date - null data"));

        Mapping<LocalDate> date = Mappings.date();
        Map<String, String> data = mmap();

        assertEquals(date.validate("date", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(date.convert("date", data), null);
    }

    @Test
    public void testDate_EmptyData() {
        System.out.println(green(">> date - empty data"));

        Mapping<LocalDate> date = Mappings.date();
        Map<String, String> data = mmap(entry("date", ""));

        assertEquals(date.validate("date", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(date.convert("date", data), null);
    }

    // datetime test

    @Test
    public void testDatetime_InvalidData() {
        System.out.println(green(">> datetime - invalid data"));

        Mapping<LocalDateTime> datetime = Mappings.datetime().label("xx");
        Map<String, String> data = mmap(entry("datetime", "5/3/2003"));

        assertEquals(datetime.validate("datetime", data, messages, Options.EMPTY),
                Arrays.asList(entry("datetime", "'xx' must satisfy any of following: " +
                        "['5/3/2003' not a date long, '5/3/2003' must be 'yyyy-MM-dd'T'HH:mm:ss.SSS']")));
    }

    @Test
    public void testDatetime_ValidData() {
        System.out.println(green(">> datetime - valid data"));

        long timestamp = System.currentTimeMillis();
        LocalDateTime datetimeObj = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC"));
        Mapping<LocalDateTime> datetime = Mappings.datetime();
        Map<String, String> data = mmap(entry("datetime", datetimeObj.toString()));

        assertEquals(datetime.validate("datetime", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(datetime.convert("datetime", data), datetimeObj);

        Map<String, String> data1 = mmap(entry("datetime", "" + timestamp));
        assertEquals(datetime.validate("datetime", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(datetime.convert("datetime", data1), datetimeObj);
    }

    @Test
    public void testDatetime_NullData() {
        System.out.println(green(">> datetime - null data"));

        Mapping<LocalDateTime> datetime = Mappings.datetime();
        Map<String, String> data = mmap();

        assertEquals(datetime.validate("datetime", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(datetime.convert("datetime", data), null);
    }

    @Test
    public void testDatetime_EmptyData() {
        System.out.println(green(">> datetime - empty data"));

        Mapping<LocalDateTime> datetime = Mappings.datetime();
        Map<String, String> data = mmap(entry("datetime", ""));

        assertEquals(datetime.validate("datetime", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(datetime.convert("datetime", data), null);
    }

    // time test

    @Test
    public void testTime_InvalidData() {
        System.out.println(green(">> time - invalid data"));

        Mapping<LocalTime> time = Mappings.time().label("xx");
        Map<String, String> data = mmap(entry("time", "5/3/2003"));

        assertEquals(time.validate("time", data, messages, Options.EMPTY),
                Arrays.asList(entry("time", "'xx' must satisfy any of following: " +
                        "['5/3/2003' not a date long, '5/3/2003' must be 'HH:mm:ss.SSS']")));
    }

    @Test
    public void testTime_ValidData() {
        System.out.println(green(">> time - valid data"));

        long timestamp = System.currentTimeMillis();
        LocalTime timeObj = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC")).toLocalTime();
        Mapping<LocalTime> time = Mappings.time();
        Map<String, String> data = mmap(entry("time", timeObj.toString()));

        assertEquals(time.validate("time", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(time.convert("time", data), timeObj);

        Map<String, String> data1 = mmap(entry("time", "" + timestamp));
        assertEquals(time.validate("time", data1, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(time.convert("time", data1), timeObj);
    }

    @Test
    public void testTime_NullData() {
        System.out.println(green(">> time - null data"));

        Mapping<LocalTime> time = Mappings.time();
        Map<String, String> data = mmap();

        assertEquals(time.validate("time", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(time.convert("time", data), null);
    }

    @Test
    public void testTime_EmptyData() {
        System.out.println(green(">> time - empty data"));

        Mapping<LocalTime> time = Mappings.time();
        Map<String, String> data = mmap(entry("time", ""));

        assertEquals(time.validate("time", data, messages, Options.EMPTY),
                Collections.EMPTY_LIST);
        assertEquals(time.convert("time", data), null);
    }

}
