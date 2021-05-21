package com.github.tminglei.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.tminglei.bind.spi.*;

import static com.github.tminglei.bind.Framework.*;
import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * pre-defined mappings
 */
public class Mappings implements Const {
    private static final Logger logger = LoggerFactory.getLogger(Mappings.class);

    private Mappings() {}

    ///////////////////////////////////  pre-defined field mappings  ////////////////////////

    /**
     * (convert to String) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<String> text(Constraint... constraints) {
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(Function.identity()),
                new MappingMeta(MAPPING_STRING, String.class)
            ).constraint(constraints);
        }

    /**
     * (convert to Boolean) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<Boolean> bool(Constraint... constraints) {
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? false : Boolean.parseBoolean(s)
                ), new MappingMeta(MAPPING_BOOLEAN, Boolean.class)
            ).constraint(parsing(Boolean::parseBoolean, "error.boolean", true))
                .constraint(constraints);
        }

    /**
     * (convert to Integer) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<Integer> intv(Constraint... constraints) {
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0 : Integer.parseInt(s)
                ), new MappingMeta(MAPPING_INT, Integer.class)
            ).constraint(parsing(Integer::parseInt, "error.number", true))
                .constraint(constraints);
        }

    /**
     * (convert to Double) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<Double> doublev(Constraint... constraints) {
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0.0d : Double.parseDouble(s)
                ), new MappingMeta(MAPPING_DOUBLE, Double.class)
            ).constraint(parsing(Double::parseDouble, "error.double", true))
                .constraint(constraints);
        }

    /**
     * (convert to Float) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<Float> floatv(Constraint... constraints) {
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0.0f : Float.parseFloat(s)
                ), new MappingMeta(MAPPING_FLOAT, Float.class)
            ).constraint(parsing(Float::parseFloat, "error.float", true))
                .constraint(constraints);
        }

    /**
     * (convert to Long) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<Long> longv(Constraint... constraints) {
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0l : Long.parseLong(s)
                ), new MappingMeta(MAPPING_LONG, Long.class)
            ).constraint(parsing(Long::parseLong, "error.long", true))
                .constraint(constraints);
        }

    /**
     * (convert to BigDecimal) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<BigDecimal> bigDecimal(Constraint... constraints) {
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? BigDecimal.ZERO : new BigDecimal(s)
                ), new MappingMeta(MAPPING_BIG_DECIMAL, BigDecimal.class)
            ).constraint(parsing(BigDecimal::new, "error.bigdecimal", true))
                .constraint(constraints);
        }

    /**
     * (convert to BigInteger) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<BigInteger> bigInt(Constraint... constraints) {
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? BigInteger.ZERO : new BigInteger(s)
                ), new MappingMeta(MAPPING_BIG_INTEGER, BigInteger.class)
            ).constraint(parsing(BigInteger::new, "error.bigint", true))
                .constraint(constraints);
        }

    /**
     * (convert to UUID) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<UUID> uuid(Constraint... constraints) {
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? null : UUID.fromString(s)
                ), new MappingMeta(MAPPING_UUID, UUID.class)
            ).constraint(parsing(UUID::fromString, "error.uuid", true))
                .constraint(constraints);
        }

    /**
     * (convert to LocalDate) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<LocalDate> date(Constraint... constraints) {
        return date("yyyy-MM-dd", constraints);
    }
    public static Mapping<LocalDate> date(String pattern, Constraint... constraints) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s -> {
                    if (isEmptyStr(s)) return null;
                    else if (s.matches("^[\\d]+$")) {
                        Instant instant = new Date(Long.parseLong(s)).toInstant();
                        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC")).toLocalDate();
                    } else {
                        return LocalDate.parse(s, formatter);
                    }
                }), new MappingMeta(MAPPING_DATE, LocalDate.class)
            ).constraint(anyPassed(
                    parsing(s -> new Date(Long.parseLong(s)), "'%s' not a date long", false),
                    parsing(formatter::parse, "error.pattern", true, pattern)
                )).constraint(constraints);
        }

    /**
     * (convert to LocalDateTime) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<LocalDateTime> datetime(Constraint... constraints) {
        return datetime("yyyy-MM-dd'T'HH:mm:ss.SSS", constraints);
    }
    public static Mapping<LocalDateTime> datetime(String pattern, Constraint... constraints) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s -> {
                    if (isEmptyStr(s)) return null;
                    else if (s.matches("^[\\d]+$")) {
                        Instant instant = new Date(Long.parseLong(s)).toInstant();
                        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
                    } else {
                        return LocalDateTime.parse(s, formatter);
                    }
                }), new MappingMeta(MAPPING_DATE_TIME, LocalDateTime.class)
            ).constraint(anyPassed(
                    parsing(s -> new Date(Long.parseLong(s)), "'%s' not a date long", false),
                    parsing(formatter::parse, "error.pattern", true, pattern)
                )).constraint(constraints);
        }

    /**
     * (convert to LocalTime) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Mapping<LocalTime> time(Constraint... constraints) {
        return time("HH:mm:ss.SSS", constraints);
    }
    public static Mapping<LocalTime> time(String pattern, Constraint... constraints) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return new FieldMapping(
                InputMode.SINGLE,
                mkSimpleConverter(s -> {
                    if (isEmptyStr(s)) return null;
                    else if (s.matches("^[\\d]+$")) {
                        Instant instant = new Date(Long.parseLong(s)).toInstant();
                        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC")).toLocalTime();
                    } else {
                        return LocalTime.parse(s, formatter);
                    }
                }), new MappingMeta(MAPPING_TIME, LocalTime.class)
            ).constraint(anyPassed(
                    parsing(s -> new Date(Long.parseLong(s)), "'%s' not a date long", false),
                    parsing(formatter::parse, "error.pattern", true, pattern)
                )).constraint(constraints);
        }

    /////////////////////////////// pre-defined general usage mappings  ///////////////////////

    /**
     * (mapping) ignore input value(s), and return 'instead' value
     * NOTE: all constraints added to the outer 'ignored' mapping, will be ignored
     * @param instead the value to be used
     * @param <T> base type
     * @return new created mapping
     */
    public static <T> Mapping<T> ignored(T instead) {
        String mname = "ignored to " + instead;
        return new FieldMapping<>(
                InputMode.POLYMORPHIC,
                ((name, data) -> instead),
                new MappingMeta(mname, instead.getClass())
            ).options(o -> o._ignoreConstraints(true));
        }

    /**
     * (mapping) return default value, if input value is null or empty
     * NOTE: all options operations will be delegate to 'base' mapping
     * @param base base mapping
     * @param defaultVal default value to be used when the related input is empty
     * @param constraints constraints
     * @param <T> base type
     * @return new created mapping
     */
    public static <T> Mapping<T> defaultv(Mapping<T> base, T defaultVal, Constraint... constraints) {
        return optional(base, constraints).map(o -> o.orElse(defaultVal));
    }

    /**
     * (mapping) wrap a base mapping, and return an optional value instead of original value
     * @param base base mapping
     * @param constraints constraints
     * @param <T> base type
     * @return new created mapping
     */
    public static <T> Mapping<Optional<T>> optional(Mapping<T> base, Constraint... constraints) {
        String mname = "optional " + base.meta().name;
        return new FieldMapping<Optional<T>>(
                base.options()._inputMode(),
                ((name, data) -> {
                    logger.debug("optional - converting {}", name);

                    if (isEmptyInput(name, data, base.options()._inputMode())) {
                        return Optional.empty();
                    } else return Optional.of(base.convert(name, data));
                }),
                ((name, data, messages, options) -> {
                    logger.debug("optional - validating {}", name);

                    if (isEmptyInput(name, data, base.options()._inputMode())) {
                        return Collections.emptyList();
                    } else { // merge the optional's constraints/label to base mapping then do validating
                        return base.options(o -> o.append_constraints(options._constraints()))
                                .options(o -> o._label(o._label().orElse(options._label().orElse(null))))
                                .validate(name, data, messages, options);
                    }
                }), new MappingMeta(mname, Optional.class, base)
            ).options(o -> o._ignoreConstraints(true))
                .constraint(constraints);
        }

    /**
     * (mapping) convert to list of values
     * @param base base mapping
     * @param constraints constraints
     * @param <T> base type
     * @return new created mapping
     */
    public static <T> Mapping<List<T>> list(Mapping<T> base, Constraint... constraints) {
        String mname = "list of " + base.meta().name;
        return new FieldMapping<>(
                InputMode.MULTIPLE,
                ((name, data) -> {
                    logger.debug("list - converting {}", name);

                    return indexes(name, data).stream()
                            .map(i -> base.convert(name + "[" + i + "]", data))
                            .collect(Collectors.toList());
                }),
                ((name, data, messages, options) -> {
                    logger.debug("list - validating {}", name);

                    return indexes(name, data).stream()
                            .flatMap(i -> base.validate(name + "[" + i + "]", data, messages, options).stream())
                            .collect(Collectors.toList());
                }), new MappingMeta(mname, List.class, base)
            ).constraint(constraints);
        }

    /**
     * (mapping) convert to map of values
     * @param vBase base mapping for map key
     * @param constraints constraints
     * @param <V> base value type
     * @return new created mapping
     */
    public static <V> Mapping<Map<String, V>> map(Mapping<V> vBase, Constraint... constraints) {
        return map(text(), vBase, constraints);
    }
    public static <K, V> Mapping<Map<K, V>> map(Mapping<K> kBase, Mapping<V> vBase, Constraint... constraints) {
        String mname = "map of " + kBase.meta().name + " -> " + vBase.meta().name;
        return new FieldMapping<>(
                InputMode.MULTIPLE,
                ((name, data) -> {
                    logger.debug("map - converting {}", name);

                    return keys(name, data).stream()
                        .map(key -> {
                            String keyName = isEmptyStr(name) ? key : name + "." + key;
                            String unquotedKey = key.replaceAll("^\"?([^\"]+)\"?$", "$1");
                            return entry(
                                kBase.convert(key, newmap(entry(key, unquotedKey))),
                                vBase.convert(keyName, data)
                            );
                        })
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));
                }),
                ((name, data, messages, options) -> {
                    logger.debug("map - validating {}", name);

                    return keys(name, data).stream()
                        .flatMap(key -> {
                            String keyName = isEmptyStr(name) ? key : name + "." + key;
                            String unquotedKey = key.replaceAll("^\"?([^\"]+)\"?$", "$1");
                            return mergeList(
                                kBase.validate(key, newmap(entry(key, unquotedKey)), messages, options),
                                vBase.validate(keyName, data, messages, options)
                            ).stream();
                        })
                        .collect(Collectors.toList());
                }), new MappingMeta(mname, Map.class, kBase, vBase)
            ).constraint(constraints);
        }

}
