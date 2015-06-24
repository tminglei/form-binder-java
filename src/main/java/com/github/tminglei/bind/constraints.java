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
public interface constraints {

    /////////////////////////////////////  pre-defined constraints  /////////////////////////

    default framework.Constraint required(String message) {
        return (name, data, messages, options) -> {
            if (isEmptyInput(name, data, options._inputMode())) {
                String msgTemplate = message != null ? message : messages.get("error.required");
                String label = getLabel(name, messages, options);
                return Arrays.asList(
                    entry(name, String.format(msgTemplate, label))
                );
            } else return Collections.EMPTY_LIST;
        };
    }

    default framework.Constraint maxlength(int length, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (vString != null && vString.length() > length) {
                String msgTemplate = message != null ? message : messages.get("error.maxlength");
                return String.format(msgTemplate, vString, length);
            } else return null;
        });
    }

    default framework.Constraint minlength(int length, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (vString != null && vString.length() < length) {
                String msgTemplate = message != null ? message : messages.get("error.minlength");
                return String.format(msgTemplate, vString, length);
            } else return null;
        });
    }

    default framework.Constraint length(int length, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (vString != null && vString.length() != length) {
                String msgTemplate = message != null ? message : messages.get("error.length");
                return String.format(msgTemplate, vString, length);
            } else return null;
        });
    }

    default framework.Constraint oneOf(Collection<String> values, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (!values.contains(vString)) {
                String msgTemplate = message != null ? message : messages.get("error.oneof");
                return String.format(msgTemplate, vString, values);
            } else return null;
        });
    }

    default framework.Constraint email(String message) {
        return pattern(PATTERN_EMAIL, message);
    }

    default framework.Constraint index(String message) {
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

    default framework.Constraint pattern(String pattern, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (vString != null && !vString.matches(pattern)) {
                String msgTemplate = message != null ? message : messages.get("error.pattern");
                return String.format(msgTemplate, vString, pattern);
            } else return null;
        });
    }

    default framework.Constraint patternNot(String pattern, String message) {
        return mkSimpleConstraint((label, vString, messages) -> {
            if (vString != null && vString.matches(pattern)) {
                String msgTemplate = message != null ? message : messages.get("error.pattern");
                return String.format(msgTemplate, vString, pattern);
            } else return null;
        });
    }

    ///////////////////////////////////  pre-defined extra constraints  //////////////////////

    default <T extends Comparable<T>> framework.ExtraConstraint<T>
                min(T minVal, String message) {
        return (label, value, messages) -> {
            if (value.compareTo(minVal) < 0) {
                String msgTemplate = message != null ? message : messages.get("error.min");
                return Arrays.asList(String.format(msgTemplate, label, minVal));
            } else return Collections.EMPTY_LIST;
        };
    }

    default <T extends Comparable<T>> framework.ExtraConstraint<T>
                max(T miaxVal, String message) {
        return (label, value, messages) -> {
            if (value.compareTo(miaxVal) > 0) {
                String msgTemplate = message != null ? message : messages.get("error.max");
                return Arrays.asList(String.format(msgTemplate, label, miaxVal));
            } else return Collections.EMPTY_LIST;
        };
    }

}
