package com.github.tminglei.bind;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by tminglei on 6/21/15.
 */
public class FrameworkUtils {

    ////////////////////////////////////////////////////////////////////////////
    public static final framework.Constraint PassValidating
            = (name, data, messages, options) -> Collections.EMPTY_LIST;

    public static <T> List<T> unmodifiableList(List<T> list) {
        return list == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(list);
    }

    public static boolean isEmptyStr(String str) {
        return str == null || str.trim().equals("");
    }

    public static boolean isEmptyInput(String name, Map<String, String> data,
                                       framework.InputMode inputMode) {
        if (inputMode == framework.InputMode.SOLO_INPUT)
            return isEmptyStr(data.get(name));
        else {
            String prefix1 = name + ".";
            String prefix2 = name + "[";
            long subInputCount = data.keySet().stream()
                    .filter(k -> k.startsWith(prefix1) || k.startsWith(prefix2))
                    .count();
            return inputMode == framework.InputMode.POLY_INPUT
                    ? isEmptyStr(data.get(name)) && subInputCount == 0
                    : subInputCount == 0;
        }
    }

    public static String getFirstNotEmpty(String... values) {
        for(String s : values) {
            if (s != null) return s;
        }
        return null;
    }

    static Pattern OBJ_ELEMENT_NAME = Pattern.compile("^(.*)\\.([^\\.]+)$");
    static Pattern ARR_ELEMENT_NAME = Pattern.compile("^(.*)\\[([\\d]+)\\]$");
    // return (parent, name, isArray:false) or (name, index, isArray:true)
    static String[] splitName(String name) {
        Matcher m = ARR_ELEMENT_NAME.matcher(name);
        if (m.matches()) {
            return new String[]{ m.group(1), m.group(2), Boolean.TRUE.toString() };
        } else if ((m = OBJ_ELEMENT_NAME.matcher(name)).matches()) {
            return new String[]{ m.group(1), m.group(2), Boolean.FALSE.toString() };
        } else {
            return new String[]{ "", name, Boolean.FALSE.toString() };
        }
    }

    ///

    // make an internal converter from `(vString) => value`
    public static <T> BiFunction<String, Map<String, String>, T>
            mkSimpleConverter(Function<String, T> convert) {
        return (name, data) -> convert.apply(data.get(name));
    }

    // make a constraint from `(label, vString, messages) => [error]` (ps: vString may be NULL/EMPTY)
    public static framework.Constraint
            mkSimpleConstraint(framework.SimpleConstraint validate) {
        return (name, data, messages, options) -> {
            String label = getLabel(name, messages, options);
            String error = validate.apply(label, data.get(name), messages);
            return isEmptyStr(error) ? Collections.EMPTY_LIST
                    : Arrays.asList(new framework.ErrMessage(name, error));
        };
    }

    // make a pre-processor from `(inputString) => outputString` (ps: inputString may be NULL/EMPTY)
    public static framework.PreProcessor
            mkSimplePreProcessor(Function<String, String> process) {
        return (name, data, options) -> {
            data.put(name, process.apply(data.get(name)));
            return data;
        };
    }

    ///

    // i18n on: use i18n label, if exists; else use label; else use last field name from full name
    // i18n off: use label; else use last field name from full name
    public static String getLabel(String fullname, framework.Messages messages, Options options) {
        String[] parts = splitName(fullname);   // parts: (parent, name, isArray)
        boolean isArray = Boolean.parseBoolean(parts[2]);
        String defaultLabel = isArray
                ? splitName(parts[0])[1] + "[" + parts[1] + "]"
                : parts[1];

        if (options.i18n() == Boolean.TRUE) {
            return getFirstNotEmpty(
                    options._label() != null ? messages.get(options._label()) : null,
                    options._label(),
                    defaultLabel
                );
        } else {
            return getFirstNotEmpty(
                    options._label(),
                    defaultLabel
                );
        }
    }

    // make a Constraint which will try to parse and collect errors
    public static <T> framework.Constraint
            parsing(Function<String, T> parse, String messageKey, String pattern) {
        return mkSimpleConstraint(((label, vString, messages) -> {
            if (isEmptyStr(vString)) return null;
            else {
                try {
                    parse.apply(vString);
                    return null;
                } catch (Exception ex) {
                    return String.format(messages.get(messageKey), label,
                            pattern == null ? "" : pattern);
                }
            }
        }));
    }

    // Computes the available indexes for the given key in this set of data.
    public static List<Integer> indexes(String name, Map<String, String> data) {
        Pattern keyPattern = Pattern.compile("^" + Pattern.quote(name) + "\\[(\\d+)\\].*$");
        return data.keySet().stream()
                .map(key -> {
                    Matcher m = keyPattern.matcher(key);
                    return m.matches() ? Integer.parseInt(m.group(1))
                            : -1;
                }).filter(i -> i >= 0)
                .sorted()
                .collect(Collectors.toList());
    }

    // Computes the available keys for the given prefix in this set of data.
    public static List<String> keys(String prefix, Map<String, String> data) {
        Pattern keyPattern = Pattern.compile("^" + Pattern.quote(prefix) + "\\.(\"?[^\\.\"]+\"?).*$");
        return data.keySet().stream()
                .map(key -> {
                    Matcher m = keyPattern.matcher(key);
                    return m.matches() ? m.group(1)
                            : null;
                }).filter(k -> k != null)
                .collect(Collectors.toList());
    }
}
