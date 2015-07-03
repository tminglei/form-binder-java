package com.github.tminglei.bind;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * pre-defined constraints/extra-constraints
 */
public class Constraints {

    /////////////////////////////////////  pre-defined constraints  /////////////////////////

    public static Framework.Constraint required() {
        return required(null);
    }
    public static Framework.Constraint required(String message) {
        return (name, data, messages, options) -> {
            if (isEmptyInput(name, data, options._inputMode())) {
                String errMessage;
                // wrong input, e.g. required single but found multiple, required multiple but found single
                if (!isEmptyInput(name, data, Framework.InputMode.POLYMORPHIC)) {
                    String msgTemplate = messages.get("error.wronginput");
                    String simple = getLabel("simple", messages, options);
                    String compound = getLabel("compound", messages, options);

                    if (options._inputMode() == Framework.InputMode.SINGLE) {
                        errMessage = String.format(msgTemplate, simple, compound);
                    } else {
                        errMessage = String.format(msgTemplate, compound, simple);
                    }
                } else {
                    String msgTemplate = message != null ? message : messages.get("error.required");
                    String label = getLabel(name, messages, options);

                    errMessage = String.format(msgTemplate, label);
                }

                return Arrays.asList(
                    entry(name, errMessage)
                );
            } else return Collections.EMPTY_LIST;
        };
    }

    public static Framework.Constraint maxlength(int length) {
        return maxlength(length, null);
    }
    public static Framework.Constraint maxlength(int length, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (vString != null && vString.length() > length) {
                String msgTemplate = message != null ? message : messages.get("error.maxlength");
                return String.format(msgTemplate, vString, length);
            } else return null;
        });
    }

    public static Framework.Constraint minlength(int length) {
        return minlength(length, null);
    }
    public static Framework.Constraint minlength(int length, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (vString != null && vString.length() < length) {
                String msgTemplate = message != null ? message : messages.get("error.minlength");
                return String.format(msgTemplate, vString, length);
            } else return null;
        });
    }

    public static Framework.Constraint length(int length) {
        return length(length, null);
    }
    public static Framework.Constraint length(int length, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (vString != null && vString.length() != length) {
                String msgTemplate = message != null ? message : messages.get("error.length");
                return String.format(msgTemplate, vString, length);
            } else return null;
        });
    }

    public static Framework.Constraint oneOf(Collection<String> values) {
        return oneOf(values, null);
    }
    public static Framework.Constraint oneOf(Collection<String> values, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (!values.contains(vString)) {
                String msgTemplate = message != null ? message : messages.get("error.oneof");
                return String.format(msgTemplate, vString, values);
            } else return null;
        });
    }

    public static Framework.Constraint email() {
        return email(null);
    }
    public static Framework.Constraint email(String message) {
        return pattern(PATTERN_EMAIL, message);
    }

    public static Framework.Constraint indexInKey() {
        return indexInKey(null);
    }
    public static Framework.Constraint indexInKey(String message) {
        return (name, data, messages, options) -> {
            String msgTemplate = message != null ? message : messages.get("error.index");
            return data.keySet().stream()
                    .filter(key -> key.startsWith(name))
                    .map(key -> {
                        Matcher m = PATTERN_ILLEGAL_INDEX.matcher(key.substring(name.length()));
                        if (m.matches()) {
                            return entry(key, NAME_ERR_PREFIX + String.format(msgTemplate, key, m.group(1)));
                        } else return null;
                    })
                    .filter(err -> err != null)
                    .collect(Collectors.toList());
        };
    }

    public static Framework.Constraint pattern(String pattern) {
        return pattern(pattern, null);
    }
    public static Framework.Constraint pattern(String pattern, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (vString != null && !vString.matches(pattern)) {
                String msgTemplate = message != null ? message : messages.get("error.pattern");
                return String.format(msgTemplate, vString, pattern);
            } else return null;
        });
    }

    public static Framework.Constraint patternNot(String pattern) {
        return patternNot(pattern, null);
    }
    public static Framework.Constraint patternNot(String pattern, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (vString != null && vString.matches(pattern)) {
                String msgTemplate = message != null ? message : messages.get("error.patternnot");
                return String.format(msgTemplate, vString, pattern);
            } else return null;
        });
    }

    ///////////////////////////////////  pre-defined extra constraints  //////////////////////

    public static <T extends Comparable<T>> Framework.ExtraConstraint<T>
                min(T minVal) {
        return min(minVal, null);
    }
    public static <T extends Comparable<T>> Framework.ExtraConstraint<T>
                min(T minVal, String message) {
        return (label, value, messages) -> {
            if (value.compareTo(minVal) < 0) {
                String msgTemplate = message != null ? message : messages.get("error.min");
                return Arrays.asList(String.format(msgTemplate, label, minVal));
            } else return Collections.EMPTY_LIST;
        };
    }

    public static <T extends Comparable<T>> Framework.ExtraConstraint<T>
                max(T maxVal) {
        return max(maxVal, null);
    }
    public static <T extends Comparable<T>> Framework.ExtraConstraint<T>
                max(T miaxVal, String message) {
        return (label, value, messages) -> {
            if (value.compareTo(miaxVal) > 0) {
                String msgTemplate = message != null ? message : messages.get("error.max");
                return Arrays.asList(String.format(msgTemplate, label, miaxVal));
            } else return Collections.EMPTY_LIST;
        };
    }

}
