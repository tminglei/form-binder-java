package com.github.tminglei.bind;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * utilities for framework internal usages
 */
public class FrameworkUtils {
    private static final Logger logger = LoggerFactory.getLogger(FrameworkUtils.class);

    public static final Pattern PATTERN_ILLEGAL_INDEX = Pattern.compile(".*\\[(\\d*[^\\d\\[\\]]+\\d*)+\\].*");
    /** original from: http://howtodoinjava.com/2014/11/11/java-regex-validate-email-address/ */
    public static final String  PATTERN_EMAIL = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    ////////////////////////////////////////////////////////////////////////////
    public static final Framework.Constraint PassValidating
            = (name, data, messages, options) -> Collections.EMPTY_LIST;

    public static <T> List<T> unmodifiableList(List<T> list) {
        return list == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(list);
    }

    public static <T> List<T> appendList(List<T> list, T... others) {
        List<T> result = list == null ? new ArrayList<>() : new ArrayList<>(list);
        result.addAll(Arrays.asList(others));
        return result;
    }
    public static <T> List<T> mergeList(List<T> list, List<T>... others) {
        List<T> result = list == null ? new ArrayList<>() : new ArrayList<>(list);
        for(List<T> other : others) result.addAll(other);
        return result;
    }
    // make Map.Entry
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry(key, value);
    }
    // make map
    public static <K, V> Map<K, V> mmap(Map.Entry<K, V>... entries) {
        Map<K, V> result = new HashMap<>();
        for(Map.Entry<K, V> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static boolean isEmptyStr(String str) {
        return str == null || str.trim().equals("") || str.equalsIgnoreCase("null");
    }

    public static boolean isEmptyInput(String name, Map<String, String> data,
                                       Framework.InputMode inputMode) {
        logger.trace("checking empty input for {}", name);

        if (inputMode == Framework.InputMode.SINGLE)
            return isEmptyStr(data.get(name));
        else {
            String prefix1 = isEmptyStr(name) ? "" : name + ".";
            String prefix2 = isEmptyStr(name) ? "" : name + "[";
            long subInputCount = data.keySet().stream()
                    .filter(k -> k.startsWith(prefix1) || k.startsWith(prefix2))
                    .count();
            return inputMode == Framework.InputMode.MULTIPLE ? subInputCount == 0
                    : isEmptyStr(data.get(name)) && subInputCount == 0;
        }
    }

    static final Pattern OBJ_ELEMENT_NAME = Pattern.compile("^(.*)\\.([^\\.]+)$");
    static final Pattern ARR_ELEMENT_NAME = Pattern.compile("^(.*)\\[([\\d]+)\\]$");
    // return (parent, name, isArray:false) or (name, index, isArray:true)
    static String[] splitName(String name) {
        logger.trace("splitting name for {}", name);

        Matcher m = ARR_ELEMENT_NAME.matcher(name);
        if (m.matches()) {
            return new String[]{ m.group(1), m.group(2), Boolean.TRUE.toString() };
        } else if ((m = OBJ_ELEMENT_NAME.matcher(name)).matches()) {
            return new String[]{ m.group(1), m.group(2), Boolean.FALSE.toString() };
        } else {
            return new String[]{ "", name, Boolean.FALSE.toString() };
        }
    }

    // find work object specified by name, create and attach it if not exists
    static Object workObject(Map<String, Object> workList, String name, boolean isArray) {
        logger.trace("get working object for {}", name);

        if (workList.get(name) != null) return workList.get(name);
        else {
            String[] parts = splitName(name);   // parts: (parent, name, isArray)
            Map<String, Object> parentObj = (Map<String, Object>) workObject(workList, parts[0], false);
            Object theObj = isArray ? new ArrayList<String>() : new HashMap<String, Object>();

            parentObj.put(parts[1], theObj);
            workList.put(name, theObj);

            return theObj;
        }
    }

    ///

    // make an internal converter from `(vString) => value`
    public static <T> BiFunction<String, Map<String, String>, T>
            mkSimpleConverter(Function<String, T> convert) {
        return (name, data) -> convert.apply(data.get(name));
    }

    // make a constraint from `(label, vString, messages) => [error]` (ps: vString may be NULL/EMPTY)
    public static Framework.Constraint
            mkSimpleConstraint(Framework.SimpleConstraint validate) {
        return ((name, data, messages, options) -> {
            if (options._inputMode() != Framework.InputMode.SINGLE)
                throw new IllegalArgumentException("The constraint shouldn't be used to non-single input mapping");

            String label = getLabel(name, messages, options);
            String error = validate.apply(label, data.get(name), messages);
            return isEmptyStr(error) ? Collections.EMPTY_LIST
                    : Arrays.asList(entry(name, error));
        });
    }

    ///

    public static Map<String, String>
            processDataRec(String prefix, Map<String, String> data, Options options,
                           List<Framework.PreProcessor> remainingProcessors) {
        if (remainingProcessors.isEmpty()) return data;
        else {
            Framework.PreProcessor currProcessor = remainingProcessors.get(0);
            List<Framework.PreProcessor> newRemainingProcessors = remainingProcessors.subList(1, remainingProcessors.size());
            Map<String, String> newData = currProcessor.apply(prefix, data, options);
            return processDataRec(prefix, newData, options, newRemainingProcessors);
        }
    }

    public static List<Map.Entry<String, String>>
            validateRec(String name, Map<String, String> data, Framework.Messages messages, Options options,
                        List<Framework.Constraint> remainingValidators) {
        if (remainingValidators.isEmpty()) return Collections.EMPTY_LIST;
        else {
            Framework.Constraint currValidator = remainingValidators.get(0);
            List<Framework.Constraint> newRemainingValidators = remainingValidators.subList(1, remainingValidators.size());

            List<Map.Entry<String, String>> errors = currValidator.apply(name, data, messages, options);
            List<Map.Entry<String, String>> errors1 = errors.isEmpty() || options.eagerCheck().orElse(false)
                    ? validateRec(name, data, messages, options, newRemainingValidators)
                    : Collections.EMPTY_LIST;
            return mergeList(errors, errors1);
        }
    }

    public static <T> List<Map.Entry<String, String>>
            extraValidateRec(String name, T vObj, Framework.Messages messages, Options options,
                             List<Framework.ExtraConstraint<T>> remainingValidators) {
        if (remainingValidators.isEmpty()) return new ArrayList<>();
        else {
            Framework.ExtraConstraint<T> currValidator = remainingValidators.get(0);
            List<Framework.ExtraConstraint<T>> newRemainingValidators = remainingValidators.subList(1, remainingValidators.size());

            String label = getLabel(name, messages, options);
            List<Map.Entry<String, String>> errors = currValidator.apply(label, vObj, messages)
                    .stream().map(msg -> entry(name, msg))
                    .collect(Collectors.toList());
            List<Map.Entry<String, String>> errors1 = errors.isEmpty() || options.eagerCheck().orElse(false)
                    ? extraValidateRec(name, vObj, messages, options, newRemainingValidators)
                    : Collections.EMPTY_LIST;
            return mergeList(errors, errors1);
        }
    }

    // i18n on: use i18n label, if exists; else use label; else use last field name from full name
    // i18n off: use label; else use last field name from full name
    public static String getLabel(String fullname, Framework.Messages messages, Options options) {
        logger.trace("getting label for {} with i18n:{}, _label:{}", fullname, options.i18n(), options._label());

        String[] parts = splitName(fullname);   // parts: (parent, name/index, isArray)
        boolean isArray = Boolean.parseBoolean(parts[2]);
        String defaultLabel = isArray
                ? splitName(parts[0])[1] + "[" + parts[1] + "]"
                : parts[1];

        String label = options.i18n().orElse(false)
                ? options._label()
                    .flatMap(l -> Optional.ofNullable(messages.get(l)))
                    .orElse(defaultLabel)
                : options._label().orElse(defaultLabel);

        logger.trace("getting label - return {}", label);

        return label;
    }

    // make a Constraint which will try to parse and collect errors
    public static <T> Framework.Constraint
            parsing(Function<String, T> parse, String messageKey, String pattern) {
        return mkSimpleConstraint(((label, vString, messages) -> {
            logger.debug("parsing check for {}", vString);

            if (isEmptyStr(vString)) return null;
            else {
                try {
                    parse.apply(vString);
                    return null;
                } catch (Exception ex) {
                    return String.format(messages.get(messageKey), vString,
                            pattern == null ? "" : pattern);
                }
            }
        }));
    }

    // Computes the available indexes for the given key in this set of data.
    public static List<Integer> indexes(String name, Map<String, String> data) {
        logger.debug("get indexes for {}", name);

        Pattern keyPattern = Pattern.compile("^" + Pattern.quote(name) + "\\[(\\d+)\\].*$");
        return data.keySet().stream()
                .map(key -> {
                    Matcher m = keyPattern.matcher(key);
                    return m.matches() ? Integer.parseInt(m.group(1))
                            : -1;
                }).filter(i -> i >= 0)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Computes the available keys for the given prefix in this set of data.
    public static List<String> keys(String prefix, Map<String, String> data) {
        logger.debug("get keys for {}", prefix);

        Pattern keyPattern = Pattern.compile("^" + Pattern.quote(prefix) + "\\.(\"?[^\\.\"]+\"?).*$");
        return data.keySet().stream()
                .map(key -> {
                    Matcher m = keyPattern.matcher(key);
                    return m.matches() ? m.group(1)
                            : null;
                }).filter(k -> k != null)
                .distinct()
                .collect(Collectors.toList());
    }

    // Construct data map from inputting jackson json object
    public static Map<String, String> json2map(String prefix, JsonNode json) {
        logger.trace("json to map - prefix: {}", prefix);

        if (json.isArray()) {
            Map<String, String> result = new HashMap<>();
            for(int i=0; i <json.size(); i++) {
                result.putAll(json2map(prefix +"["+i+"]", json.get(i)));
            }
            return result;
        } else if (json.isObject()) {
            Map<String, String> result = new HashMap<>();
            json.fields().forEachRemaining(e -> {
                String newPrefix = isEmptyStr(prefix) ? e.getKey() : prefix + "." + e.getKey();
                result.putAll(json2map(newPrefix, e.getValue()));
            });
            return result;
        } else {
            return mmap(entry(prefix, json.asText()));
        }
    }
}
