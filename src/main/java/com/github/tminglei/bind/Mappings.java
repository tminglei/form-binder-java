package com.github.tminglei.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
                mkSimpleConverter(Function.identity())
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
                )
            ).constraint(parsing(Boolean::parseBoolean, "error.boolean", ""))
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
                )
            ).constraint(parsing(Integer::parseInt, "error.number", ""))
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
                )
            ).constraint(parsing(Double::parseDouble, "error.double", ""))
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
                )
            ).constraint(parsing(Float::parseFloat, "error.float", ""))
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
                )
            ).constraint(parsing(Long::parseLong, "error.long", ""))
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
                )
            ).constraint(parsing(BigDecimal::new, "error.bigdecimal", ""))
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
                )
            ).constraint(parsing(BigInteger::new, "error.bigint", ""))
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
                )
            ).constraint(parsing(UUID::fromString, "error.uuid", ""))
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
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? null : LocalDate.parse(s, formatter)
                )
            ).constraint(parsing(formatter::parse, "error.pattern", pattern))
                .constraint(constraints);
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
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? null : LocalDateTime.parse(s, formatter)
                )
            ).constraint(parsing(formatter::parse, "error.pattern", pattern))
                .constraint(constraints);
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
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? null : LocalTime.parse(s, formatter)
                )
            ).constraint(parsing(formatter::parse, "error.pattern", pattern))
                .constraint(constraints);
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
                ((name, data) -> instead)
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
        return new Framework.MappingWrapper<T>(base,
                ((name, data) -> {
                    logger.debug("defaultVal - do converting for {}", name);

                    if (isEmptyInput(name, data, base.options()._inputMode())) {
                        return defaultVal;
                    } else return base.convert(name, data);
                }),
                ((name, data, messages, options) -> {
                    logger.debug("defaultVal - do validating for {}", name);

                    if (isEmptyInput(name, data, base.options()._inputMode())) {
                        return Collections.emptyList();
                    } else return base.validate(name, data, messages, options);
                })
            ).constraint(constraints);
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
                    logger.debug("optional - do converting for {}", name);

                    if (isEmptyInput(name, data, base.options()._inputMode())) {
                        return Optional.empty();
                    } else return Optional.of(base.convert(name, data));
                }),
                ((name, data, messages, options) -> {
                    logger.debug("optional - do validating for {}", name);

                    if (isEmptyInput(name, data, base.options()._inputMode())) {
                        return Collections.EMPTY_LIST;
                    } else {
                        List<Map.Entry<String, String>> errors = validateRec(name, data, messages, options, options._constraints());
                        List<Map.Entry<String, String>> errors1 = errors.isEmpty() || options.eagerCheck().orElse(false)
                                ? base.validate(name, data, messages, options)
                                : Collections.EMPTY_LIST;
                        return mergeList(errors, errors1);
                    }
                })
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
                    logger.debug("list - do converting for {}", name);

                    return indexes(name, data).stream()
                            .map(i -> base.convert(name + "[" + i + "]", data))
                            .collect(Collectors.toList());
                }),
                ((name, data, messages, options) -> {
                    logger.debug("list - do validating for {}", name);

                    return indexes(name, data).stream()
                            .flatMap(i -> base.validate(name + "[" + i + "]", data, messages, options).stream())
                            .collect(Collectors.toList());
                })
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
                    logger.debug("map - do converting for {}", name);

                    return keys(name, data).stream()
                        .map(key -> entry(
                                kBase.convert(key, mmap(entry(key, key))),
                                vBase.convert(name + "." + key, data)
                        ))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));
                }),
                ((name, data, messages, options) -> {
                    logger.debug("map - do validating for {}", name);

                    return keys(name, data).stream()
                        .flatMap(key -> mergeList(
                                kBase.validate(key, mmap(entry(key, key)), messages, options),
                                vBase.validate(name + "." + key, data, messages, options)
                        ).stream())
                        .collect(Collectors.toList());
                })
            ).constraint(constraints);
        }

}
