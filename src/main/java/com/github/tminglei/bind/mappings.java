package com.github.tminglei.bind;

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
public interface mappings {

    ///////////////////////////////////  pre-defined field mappings  ////////////////////////
    default framework.Mapping<String> text(framework.Constraint... constraints) {
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(Function.identity())
            ).constraint(constraints);
        }

    default framework.Mapping<Boolean> bool(framework.Constraint... constraints) {
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? false : Boolean.parseBoolean(s)
                )
            ).constraint(parsing(Boolean::parseBoolean, "error.boolean", ""))
                .constraint(constraints);
        }

    default framework.Mapping<Integer> vInt(framework.Constraint... constraints) {
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0 : Integer.parseInt(s)
                )
            ).constraint(parsing(Integer::parseInt, "error.number", ""))
                .constraint(constraints);
        }

    default framework.Mapping<Double> vDouble(framework.Constraint... constraints) {
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0.0d : Double.parseDouble(s)
                )
            ).constraint(parsing(Double::parseDouble, "error.double", ""))
                .constraint(constraints);
        }

    default framework.Mapping<Float> vFloat(framework.Constraint... constraints) {
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0.0f : Float.parseFloat(s)
                )
            ).constraint(parsing(Float::parseFloat, "error.float", ""))
                .constraint(constraints);
        }

    default framework.Mapping<Long> vLong(framework.Constraint... constraints) {
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? 0l : Long.parseLong(s)
                )
            ).constraint(parsing(Long::parseLong, "error.long", ""))
                .constraint(constraints);
        }

    default framework.Mapping<BigDecimal> bigDecimal(framework.Constraint... constraints) {
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? BigDecimal.ZERO : new BigDecimal(s)
                )
            ).constraint(parsing(BigDecimal::new, "error.bigdecimal", ""))
                .constraint(constraints);
        }

    default framework.Mapping<BigInteger> bigInt(framework.Constraint... constraints) {
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? BigInteger.ZERO : new BigInteger(s)
                )
            ).constraint(parsing(BigInteger::new, "error.bigint", ""))
                .constraint(constraints);
        }

    default framework.Mapping<UUID> uuid(framework.Constraint... constraints) {
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? null : UUID.fromString(s)
                )
            ).constraint(parsing(UUID::fromString, "error.uuid", ""))
                .constraint(constraints);
        }

    default framework.Mapping<LocalDate> date(framework.Constraint... constraints) {
        return date("yyyy-MM-dd", constraints);
    }
    default framework.Mapping<LocalDate> date(String pattern, framework.Constraint... constraints) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? null : LocalDate.parse(s, formatter)
                )
            ).constraint(parsing(formatter::parse, "error.pattern", pattern))
                .constraint(constraints);
        }

    default framework.Mapping<LocalDateTime> datetime(framework.Constraint... constraints) {
        return datetime("yyyy-MM-dd'T'HH:mm:ss.SSS", constraints);
    }
    default framework.Mapping<LocalDateTime> datetime(String pattern, framework.Constraint... constraints) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? null : LocalDateTime.parse(s, formatter)
                )
            ).constraint(parsing(formatter::parse, "error,pattern", pattern))
                .constraint(constraints);
        }

    default framework.Mapping<LocalTime> time(framework.Constraint... constraints) {
        return time("HH:mm:ss.SSS", constraints);
    }
    default framework.Mapping<LocalTime> time(String pattern, framework.Constraint... constraints) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return new framework.FieldMapping(
                framework.InputMode.SOLO_INPUT,
                mkSimpleConverter(s ->
                    isEmptyStr(s) ? null : LocalTime.parse(s, formatter)
                )
            ).constraint(parsing(formatter::parse, "error.pattern", pattern))
                .constraint(constraints);
        }

    /////////////////////////////// pre-defined general usage mappings  ///////////////////////
    // note: all constraints added to the outer 'ignored' mapping, will be ignored
    default <T> framework.Mapping<T> ignored(T instead) {
        return new framework.FieldMapping<T>(
                framework.InputMode.POLY_INPUT,
                ((name, data) -> instead)
            ).options(o -> o._ignoreConstraints(true));
        }

    // note: all options operations will be delegate to 'base' mapping
    default <T> framework.Mapping<T> defaultVal(framework.Mapping<T> base, T defaultVal, framework.Constraint... constraints) {
        return new framework.MappingWrapper<T>(base,
                ((name, data) -> {
                    if (isEmptyInput(name, data, base.options()._inputMode())) {
                        return defaultVal;
                    } else return base.convert(name, data);
                }),
                ((name, data, messages, options) -> {
                    if (isEmptyInput(name, data, base.options()._inputMode())) {
                        return Collections.emptyList();
                    } else return base.validate(name, data, messages, options);
                })
            ).constraint(constraints);
        }

    default <T> framework.Mapping<Optional<T>> optional(framework.Mapping<T> base, framework.Constraint... constraints) {
        return new framework.FieldMapping<Optional<T>>(
                base.options()._inputMode(),
                ((name, data) -> {
                    if (isEmptyInput(name, data, base.options()._inputMode())) {
                        return Optional.empty();
                    } else return Optional.of(base.convert(name, data));
                }),
                ((name, data, messages, options) -> {
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

    default <T> framework.Mapping<List<T>> list(framework.Mapping<T> base, framework.Constraint... constraints) {
        return new framework.FieldMapping<List<T>>(
                framework.InputMode.BULK_INPUT,
                ((name, data) -> {
                    return indexes(name, data).stream()
                            .map(i -> base.convert(name + "[" + i + "]", data))
                            .collect(Collectors.toList());
                }),
                ((name, data, messages, options) -> {
                    return indexes(name, data).stream()
                            .flatMap(i -> base.validate(name + "[" + i + "]", data, messages, options).stream())
                            .collect(Collectors.toList());
                })
            ).constraint(constraints);
        }

    default <V> framework.Mapping<Map<String, V>> map(framework.Mapping<V> vBase,
                                                      framework.Constraint... constraints) {
        return map(text(), vBase, constraints);
    }
    default <K, V> framework.Mapping<Map<K, V>> map(framework.Mapping<K> kBase, framework.Mapping<V> vBase,
                                                    framework.Constraint... constraints) {
        return new framework.FieldMapping<Map<K, V>>(
                framework.InputMode.BULK_INPUT,
                ((name, data) -> {
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
