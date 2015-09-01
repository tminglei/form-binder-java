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

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * pre-defined mappings
 */
public class Mappings {
    private static final Logger logger = LoggerFactory.getLogger(Mappings.class);

    ///////////////////////////////////  pre-defined field mappings  ////////////////////////

    /**
     * (convert to String) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<String> text(Framework.Constraint... constraints) {
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(Function.identity()),
                new Framework.MappingMeta(String.class)
            ).constraint(constraints);
        }

    /**
     * (convert to Boolean) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<Boolean> vBoolean(Framework.Constraint... constraints) {
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? false : Boolean.parseBoolean(s)
                ), new Framework.MappingMeta(Boolean.class)
            ).constraint(checking(Boolean::parseBoolean, "error.boolean", true))
                .constraint(constraints);
        }

    /**
     * (convert to Integer) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<Integer> vInt(Framework.Constraint... constraints) {
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0 : Integer.parseInt(s)
                ), new Framework.MappingMeta(Integer.class)
            ).constraint(checking(Integer::parseInt, "error.number", true))
                .constraint(constraints);
        }

    /**
     * (convert to Double) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<Double> vDouble(Framework.Constraint... constraints) {
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0.0d : Double.parseDouble(s)
                ), new Framework.MappingMeta(Double.class)
            ).constraint(checking(Double::parseDouble, "error.double", true))
                .constraint(constraints);
        }

    /**
     * (convert to Float) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<Float> vFloat(Framework.Constraint... constraints) {
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0.0f : Float.parseFloat(s)
                ), new Framework.MappingMeta(Float.class)
            ).constraint(checking(Float::parseFloat, "error.float", true))
                .constraint(constraints);
        }

    /**
     * (convert to Long) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<Long> vLong(Framework.Constraint... constraints) {
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0l : Long.parseLong(s)
                ), new Framework.MappingMeta(Long.class)
            ).constraint(checking(Long::parseLong, "error.long", true))
                .constraint(constraints);
        }

    /**
     * (convert to BigDecimal) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<BigDecimal> bigDecimal(Framework.Constraint... constraints) {
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? BigDecimal.ZERO : new BigDecimal(s)
                ), new Framework.MappingMeta(BigDecimal.class)
            ).constraint(checking(BigDecimal::new, "error.bigdecimal", true))
                .constraint(constraints);
        }

    /**
     * (convert to BigInteger) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<BigInteger> bigInt(Framework.Constraint... constraints) {
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? BigInteger.ZERO : new BigInteger(s)
                ), new Framework.MappingMeta(BigInteger.class)
            ).constraint(checking(BigInteger::new, "error.bigint", true))
                .constraint(constraints);
        }

    /**
     * (convert to UUID) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<UUID> uuid(Framework.Constraint... constraints) {
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? null : UUID.fromString(s)
                ), new Framework.MappingMeta(UUID.class)
            ).constraint(checking(UUID::fromString, "error.uuid", true))
                .constraint(constraints);
        }

    /**
     * (convert to LocalDate) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<LocalDate> date(Framework.Constraint... constraints) {
        return date("yyyy-MM-dd", constraints);
    }
    public static Framework.Mapping<LocalDate> date(String pattern, Framework.Constraint... constraints) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s -> {
                    if (isEmptyStr(s)) return null;
                    else if (s.matches("^[\\d]+$")) {
                        Instant instant = new Date(Long.parseLong(s)).toInstant();
                        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC")).toLocalDate();
                    } else {
                        return LocalDate.parse(s, formatter);
                    }
                }), new Framework.MappingMeta(LocalDate.class)
            ).constraint(anyPassed(
                    checking(s -> new Date(Long.parseLong(s)), "'%s' not a date long", false),
                    checking(formatter::parse, "error.pattern", true, pattern)
                )).constraint(constraints);
        }

    /**
     * (convert to LocalDateTime) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<LocalDateTime> datetime(Framework.Constraint... constraints) {
        return datetime("yyyy-MM-dd'T'HH:mm:ss.SSS", constraints);
    }
    public static Framework.Mapping<LocalDateTime> datetime(String pattern, Framework.Constraint... constraints) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s -> {
                    if (isEmptyStr(s)) return null;
                    else if (s.matches("^[\\d]+$")) {
                        Instant instant = new Date(Long.parseLong(s)).toInstant();
                        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
                    } else {
                        return LocalDateTime.parse(s, formatter);
                    }
                }), new Framework.MappingMeta(LocalDateTime.class)
            ).constraint(anyPassed(
                    checking(s -> new Date(Long.parseLong(s)), "'%s' not a date long", false),
                    checking(formatter::parse, "error.pattern", true, pattern)
                )).constraint(constraints);
        }

    /**
     * (convert to LocalTime) mapping
     * @param constraints constraints
     * @return new created mapping
     */
    public static Framework.Mapping<LocalTime> time(Framework.Constraint... constraints) {
        return time("HH:mm:ss.SSS", constraints);
    }
    public static Framework.Mapping<LocalTime> time(String pattern, Framework.Constraint... constraints) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return new Framework.FieldMapping(
                Framework.InputMode.SINGLE,
                mkSimpleConverter(s -> {
                    if (isEmptyStr(s)) return null;
                    else if (s.matches("^[\\d]+$")) {
                        Instant instant = new Date(Long.parseLong(s)).toInstant();
                        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC")).toLocalTime();
                    } else {
                        return LocalTime.parse(s, formatter);
                    }
                }), new Framework.MappingMeta(LocalTime.class)
            ).constraint(anyPassed(
                    checking(s -> new Date(Long.parseLong(s)), "'%s' not a date long", false),
                    checking(formatter::parse, "error.pattern", true, pattern)
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
    public static <T> Framework.Mapping<T> ignored(T instead) {
        return new Framework.FieldMapping<T>(
                Framework.InputMode.POLYMORPHIC,
                ((name, data) -> instead),
                new Framework.MappingMeta(instead.getClass())
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
    public static <T> Framework.Mapping<T> defaultVal(Framework.Mapping<T> base, T defaultVal, Framework.Constraint... constraints) {
        return optional(base, constraints).mapTo(o -> o.orElse(defaultVal));
    }

    /**
     * (mapping) wrap a base mapping, and return an optional value instead of original value
     * @param base base mapping
     * @param constraints constraints
     * @param <T> base type
     * @return new created mapping
     */
    public static <T> Framework.Mapping<Optional<T>> optional(Framework.Mapping<T> base, Framework.Constraint... constraints) {
        return new Framework.FieldMapping<Optional<T>>(
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
                        return Collections.EMPTY_LIST;
                    } else { // merge the optional's constraints/label to base mapping then do validating
                        return base.options(o -> o.append_constraints(
                                    options._constraints().toArray(new Framework.Constraint[0])))
                                .options(o -> o._label(o._label().orElse(options._label().orElse(null))))
                                .validate(name, data, messages, options);
                    }
                }), new Framework.MappingMeta(Optional.class, base)
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
    public static <T> Framework.Mapping<List<T>> list(Framework.Mapping<T> base, Framework.Constraint... constraints) {
        return new Framework.FieldMapping<List<T>>(
                Framework.InputMode.MULTIPLE,
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
                }), new Framework.MappingMeta(List.class, base)
            ).constraint(constraints);
        }

    /**
     * (mapping) convert to map of values
     * @param vBase base mapping for map key
     * @param constraints constraints
     * @param <V> base value type
     * @return new created mapping
     */
    public static <V> Framework.Mapping<Map<String, V>> map(Framework.Mapping<V> vBase,
                                                      Framework.Constraint... constraints) {
        return map(text(), vBase, constraints);
    }
    public static <K, V> Framework.Mapping<Map<K, V>> map(Framework.Mapping<K> kBase, Framework.Mapping<V> vBase,
                                                    Framework.Constraint... constraints) {
        return new Framework.FieldMapping<Map<K, V>>(
                Framework.InputMode.MULTIPLE,
                ((name, data) -> {
                    logger.debug("map - converting {}", name);

                    return keys(name, data).stream()
                        .map(key -> {
                            String keyName = isEmptyStr(name) ? key : name + "." + key;
                            String unquotedKey = key.replaceAll("^\"?([^\"]+)\"?$", "$1");
                            return entry(
                                kBase.convert(key, mmap(entry(key, unquotedKey))),
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
                                kBase.validate(key, mmap(entry(key, unquotedKey)), messages, options),
                                vBase.validate(keyName, data, messages, options)
                            ).stream();
                        })
                        .collect(Collectors.toList());
                }), new Framework.MappingMeta(Map.class, kBase, vBase)
            ).constraint(constraints);
        }

}
