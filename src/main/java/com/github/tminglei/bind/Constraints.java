package com.github.tminglei.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(Constraints.class);

    /////////////////////////////////////  pre-defined constraints  /////////////////////////

    public static Framework.Constraint required() {
        return required(null);
    }
    public static Framework.Constraint required(String message) {
        return mkConstraintWithMeta(
            (name, data, messages, options) -> {
                logger.debug("checking required for {}", name);

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

                    return Arrays.asList(entry(name, errMessage));
                } else return Collections.EMPTY_LIST;
            }, mkExtensionMeta("required"));
        }

    public static Framework.Constraint maxLength(int length) {
        return maxLength(length, null);
    }
    public static Framework.Constraint maxLength(int length, String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking max-length ({}) for '{}'", length, vString);

                if (vString != null && vString.length() > length) {
                    String msgTemplate = message != null ? message : messages.get("error.maxlength");
                    return String.format(msgTemplate, vString, length);
                } else return null;
            }, mkExtensionMeta("maxLength", length));
        }

    public static Framework.Constraint minLength(int length) {
        return minLength(length, null);
    }
    public static Framework.Constraint minLength(int length, String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking min-length ({}) for '{}'", length, vString);

                if (vString != null && vString.length() < length) {
                    String msgTemplate = message != null ? message : messages.get("error.minlength");
                    return String.format(msgTemplate, vString, length);
                } else return null;
            }, mkExtensionMeta("minLength", length));
        }

    public static Framework.Constraint length(int length) {
        return length(length, null);
    }
    public static Framework.Constraint length(int length, String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking length ({}) for '{}'", length, vString);

                if (vString != null && vString.length() != length) {
                    String msgTemplate = message != null ? message : messages.get("error.length");
                    return String.format(msgTemplate, vString, length);
                } else return null;
            }, mkExtensionMeta("length", length));
        }

    public static Framework.Constraint oneOf(Collection<String> values) {
        return oneOf(values, null);
    }
    public static Framework.Constraint oneOf(Collection<String> values, String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking one of {} for '{}'", values, vString);

                if (!values.contains(vString)) {
                    String msgTemplate = message != null ? message : messages.get("error.oneof");
                    return String.format(msgTemplate, vString, values);
                } else return null;
            }, mkExtensionMeta("oneOf", values));
        }

    public static Framework.Constraint email() {
        return email(null);
    }
    public static Framework.Constraint email(String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking email for '{}'", vString);

                if (vString != null && !vString.matches(PATTERN_EMAIL)) {
                    String msgTemplate = message != null ? message : messages.get("error.email");
                    return String.format(msgTemplate, vString, PATTERN_EMAIL);
                } else return null;
            }, mkExtensionMeta("email"));
        }

    public static Framework.Constraint pattern(String pattern) {
        return pattern(pattern, null);
    }
    public static Framework.Constraint pattern(String pattern, String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking pattern '{}' for '{}'", pattern, vString);

                if (vString != null && !vString.matches(pattern)) {
                    String msgTemplate = message != null ? message : messages.get("error.pattern");
                    return String.format(msgTemplate, vString, pattern);
                } else return null;
            }, mkExtensionMeta("pattern", pattern));
        }

    public static Framework.Constraint patternNot(String pattern) {
        return patternNot(pattern, null);
    }
    public static Framework.Constraint patternNot(String pattern, String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking pattern-not '{}' for '{}'", pattern, vString);

                if (vString != null && vString.matches(pattern)) {
                    String msgTemplate = message != null ? message : messages.get("error.patternnot");
                    return String.format(msgTemplate, vString, pattern);
                } else return null;
            }, mkExtensionMeta("patternNot", pattern));
        }

    public static Framework.Constraint indexInKeys() {
        return indexInKeys(null);
    }
    public static Framework.Constraint indexInKeys(String message) {
        return mkConstraintWithMeta(
            (name, data, messages, options) -> {
                logger.debug("checking index in keys for '{}'", name);

                String msgTemplate = message != null ? message : messages.get("error.index");
                return data.keySet().stream()
                        .filter(key -> key.startsWith(name))
                        .map(key -> {
                            Matcher m = PATTERN_ILLEGAL_INDEX.matcher(key.substring(name.length()));
                            if (m.matches()) {
                                return entry(key, String.format(msgTemplate, key, m.group(1)));
                            } else return null;
                        })
                        .filter(err -> err != null)
                        .collect(Collectors.toList());
            }, mkExtensionMeta("indexInKeys"));
        }

    ///////////////////////////////////  pre-defined extra constraints  //////////////////////

    public static <T extends Comparable<T>> Framework.ExtraConstraint<T>
                min(T minVal) {
        return min(minVal, null);
    }
    public static <T extends Comparable<T>> Framework.ExtraConstraint<T>
                min(T minVal, String message) {
        return mkExtraConstraintWithMeta(
            (label, value, messages) -> {
                logger.debug("checking min value ({}) for {}", minVal, value);

                if (value.compareTo(minVal) < 0) {
                    String msgTemplate = message != null ? message : messages.get("error.min");
                    return Arrays.asList(String.format(msgTemplate, value, minVal));
                } else return Collections.EMPTY_LIST;
            }, mkExtensionMeta("min", minVal));
        }

    public static <T extends Comparable<T>> Framework.ExtraConstraint<T>
                max(T maxVal) {
        return max(maxVal, null);
    }
    public static <T extends Comparable<T>> Framework.ExtraConstraint<T>
                max(T maxVal, String message) {
        return mkExtraConstraintWithMeta(
            (label, value, messages) -> {
                logger.debug("checking max value ({}) for {}", maxVal, value);

                if (value.compareTo(maxVal) > 0) {
                    String msgTemplate = message != null ? message : messages.get("error.max");
                    return Arrays.asList(String.format(msgTemplate, value, maxVal));
                } else return Collections.EMPTY_LIST;
            }, mkExtensionMeta("max", maxVal));
        }

}
