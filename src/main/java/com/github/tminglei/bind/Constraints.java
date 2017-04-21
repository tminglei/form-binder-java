package com.github.tminglei.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.github.tminglei.bind.spi.*;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * pre-defined constraints/extra-constraints
 */
public class Constraints implements Const {
    private static final Logger logger = LoggerFactory.getLogger(Constraints.class);

    private Constraints() {}

    /////////////////////////////////////  pre-defined constraints  /////////////////////////

    public static Constraint required() {
        return required(null);
    }
    public static Constraint required(String message) {
        return mkConstraintWithMeta(
            (name, data, messages, options) -> {
                logger.debug("checking required for {}", name);

                if (isEmptyInput(name, data, options._inputMode())) {
                    String errMessage;
                    // wrong input, e.g. required single but found multiple, required multiple but found single
                    if (!isEmptyInput(name, data, InputMode.POLYMORPHIC)) {
                        String msgTemplate = messages.get("error.wronginput");
                        String simple = getLabel("simple", messages, options);
                        String compound = getLabel("compound", messages, options);

                        if (options._inputMode() == InputMode.SINGLE) {
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
                } else return Collections.emptyList();
            }, mkExtensionMeta(CONSTRAINT_REQUIRED));
        }

    public static Constraint maxLength(int length) {
        return maxLength(length, true);
    }
    public static Constraint maxLength(int length, boolean withIt) {
        return maxLength(length, null, withIt);
    }
    public static Constraint maxLength(int length, String message) {
        return maxLength(length, message, true);
    }
    public static Constraint maxLength(int length, String message, boolean withIt) {
        return mkSimpleConstraint(
                (label, vString, messages) -> {
                    logger.debug("checking max-length ({}) for '{}'", length, vString);

                    if (vString != null && ((withIt && vString.length() > length)
                            || (!withIt && vString.length() >= length))) {
                        String msgTemplate = message != null ? message : messages.get("error.maxlength");
                        return String.format(msgTemplate, vString, length, withIt);
                    } else return null;
                }, mkExtensionMeta(CONSTRAINT_MAX_LENGTH, length));
        }

    public static Constraint minLength(int length) {
        return minLength(length, true);
    }
    public static Constraint minLength(int length, boolean withIt) {
        return minLength(length, null, withIt);
    }
    public static Constraint minLength(int length, String message) {
        return minLength(length, message, true);
    }
    public static Constraint minLength(int length, String message, boolean withIt) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking min-length ({}) for '{}'", length, vString);

                if (vString != null && ((withIt && vString.length() < length)
                        || (!withIt && vString.length() <= length))) {
                    String msgTemplate = message != null ? message : messages.get("error.minlength");
                    return String.format(msgTemplate, vString, length, withIt);
                } else return null;
            }, mkExtensionMeta(CONSTRAINT_MIN_LENGTH, length));
        }

    public static Constraint length(int length) {
        return length(length, null);
    }
    public static Constraint length(int length, String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking length ({}) for '{}'", length, vString);

                if (vString != null && vString.length() != length) {
                    String msgTemplate = message != null ? message : messages.get("error.length");
                    return String.format(msgTemplate, vString, length);
                } else return null;
            }, mkExtensionMeta(CONSTRAINT_LENGTH, length));
        }

    public static Constraint oneOf(Collection<String> values) {
        return oneOf(values, null);
    }
    public static Constraint oneOf(Collection<String> values, String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking one of {} for '{}'", values, vString);

                if (!values.contains(vString)) {
                    String msgTemplate = message != null ? message : messages.get("error.oneof");
                    return String.format(msgTemplate, vString, values);
                } else return null;
            }, mkExtensionMeta(CONSTRAINT_ONE_OF, values));
        }

    public static Constraint email() {
        return email(null);
    }
    public static Constraint email(String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking email for '{}'", vString);

                if (vString != null && !vString.matches(PATTERN_EMAIL)) {
                    String msgTemplate = message != null ? message : messages.get("error.email");
                    return String.format(msgTemplate, vString, PATTERN_EMAIL);
                } else return null;
            }, mkExtensionMeta(CONSTRAINT_EMAIL));
        }

    public static Constraint pattern(String pattern) {
        return pattern(pattern, null);
    }
    public static Constraint pattern(String pattern, String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking pattern '{}' for '{}'", pattern, vString);

                if (vString != null && !vString.matches(pattern)) {
                    String msgTemplate = message != null ? message : messages.get("error.pattern");
                    return String.format(msgTemplate, vString, pattern);
                } else return null;
            }, mkExtensionMeta(CONSTRAINT_PATTERN, pattern));
        }

    public static Constraint patternNot(String pattern) {
        return patternNot(pattern, null);
    }
    public static Constraint patternNot(String pattern, String message) {
        return mkSimpleConstraint(
            (label, vString, messages) -> {
                logger.debug("checking pattern-not '{}' for '{}'", pattern, vString);

                if (vString != null && vString.matches(pattern)) {
                    String msgTemplate = message != null ? message : messages.get("error.patternnot");
                    return String.format(msgTemplate, vString, pattern);
                } else return null;
            }, mkExtensionMeta(CONSTRAINT_PATTERN_NOT, pattern));
        }

    public static Constraint indexInKeys() {
        return indexInKeys(null);
    }
    public static Constraint indexInKeys(String message) {
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
            }, mkExtensionMeta(CONSTRAINT_INDEX_IN_KEYS));
        }

    ///////////////////////////////////  pre-defined extra constraints  //////////////////////

    public static <T extends Comparable<T>> ExtraConstraint<T>
                min(T minVal) {
        return min(minVal, true);
    }
    public static <T extends Comparable<T>> ExtraConstraint<T>
                min(T minVal, boolean withIt) {
        return min(minVal, null, withIt);
    }
    public static <T extends Comparable<T>> ExtraConstraint<T>
                min(T minVal, String message) {
        return min(minVal, message, true);
    }
    public static <T extends Comparable<T>> ExtraConstraint<T>
                min(T minVal, String message, boolean withIt) {
        return mkExtraConstraintWithMeta(
            (label, value, messages) -> {
                logger.debug("checking min value ({}) for {}", minVal, value);

                if ((withIt && value.compareTo(minVal) < 0)
                        || (!withIt && value.compareTo(minVal) <= 0)) {
                    String msgTemplate = message != null ? message : messages.get("error.min");
                    return Arrays.asList(String.format(msgTemplate, value, minVal, withIt));
                } else return Collections.emptyList();
            }, new ExtensionMeta(
                    EX_CONSTRAINT_MIN,
                    "min(" + minVal + " " + (withIt ? "w/" : "w/o") + " boundary)",
                    Arrays.asList(minVal, withIt)));
        }

    public static <T extends Comparable<T>> ExtraConstraint<T>
                max(T maxVal) {
        return max(maxVal, true);
    }
    public static <T extends Comparable<T>> ExtraConstraint<T>
                max(T maxVal, boolean withIt) {
        return max(maxVal, null, withIt);
    }
    public static <T extends Comparable<T>> ExtraConstraint<T>
                max(T maxVal, String message) {
        return max(maxVal, message, true);
    }
    public static <T extends Comparable<T>> ExtraConstraint<T>
                max(T maxVal, String message, boolean withIt) {
        return mkExtraConstraintWithMeta(
            (label, value, messages) -> {
                logger.debug("checking max value ({}) for {}", maxVal, value);

                if ((withIt && value.compareTo(maxVal) > 0)
                        || (!withIt && value.compareTo(maxVal) >= 0)) {
                    String msgTemplate = message != null ? message : messages.get("error.max");
                    return Arrays.asList(String.format(msgTemplate, value, maxVal, withIt));
                } else return Collections.emptyList();
            }, new ExtensionMeta(
                EX_CONSTRAINT_MAX,
                "max(" + maxVal + " " + (withIt ? "w/" : "w/o") + " boundary)",
                Arrays.asList(maxVal, withIt)));
        }

}
