package com.github.tminglei.bind;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * pre-defined pre-processors/err-processors
 */
public class Processors {
    private static final Logger logger = LoggerFactory.getLogger(Processors.class);

    ///////////////////////////////////  pre-defined pre-processors  //////////////////////////

    public static Framework.PreProcessor trim() {
        return ((prefix, data, options) -> {
            logger.debug("trimming '{}'", prefix);

            return data.entrySet().stream()
                    .map(e -> {
                        if (!e.getKey().startsWith(prefix)) return e;
                        else {
                            String v = e.getValue();
                            return entry(
                                    e.getKey(),
                                    v != null ? v.trim() : ""
                            );
                        }
                    }).collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
            });
        }

    public static Framework.PreProcessor omit(String str) {
        return replaceMatched(Pattern.quote(str), "");
    }

    public static Framework.PreProcessor omitLeft(String str) {
        return replaceMatched("^" + Pattern.quote(str), "");
    }

    public static Framework.PreProcessor omitRight(String str) {
        return replaceMatched(Pattern.quote(str) + "$", "");
    }

    public static Framework.PreProcessor omitRedundant(String str) {
        return replaceMatched("["+Pattern.quote(str)+"]+", str);
    }

    public static Framework.PreProcessor omitMatched(String pattern) {
        return replaceMatched(pattern, "");
    }

    public static Framework.PreProcessor replaceMatched(String pattern, String replacement) {
        return ((prefix, data, options) -> {
            logger.debug("replacing '{}' with '{}'", pattern, replacement);

            return data.entrySet().stream()
                    .map(e -> {
                        if (!e.getKey().startsWith(prefix)) return e;
                        else {
                            String v = e.getValue();
                            return entry(
                                e.getKey(),
                                v != null ? v.replaceAll(pattern, replacement) : ""
                            );
                        }
                    }).collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
            });
        }

    /**
     * expand json string to map of data
     * @return new created pre-processor
     */
    public static Framework.PreProcessor expandJson() {
        return expandJson(null);
    }
    public static Framework.PreProcessor expandJson(String prefix) {
        return ((prefix1, data, options) -> {
            logger.debug("expanding json at {}", (prefix == null ? prefix1 : prefix));

            String thePrefix = prefix == null ? prefix1 : prefix;
            String jsonStr  = data.get(thePrefix);

            Map<String, String> newData = new HashMap<>(data);
            newData.remove(thePrefix); // remove old one to avoid disturbing other processing

            try {
                if (isEmptyStr(jsonStr)) {
                    logger.warn("json string is '{}'", jsonStr);
                } else {
                    JsonNode json = new ObjectMapper().readTree(jsonStr);
                    newData.putAll(json2map(thePrefix, json));
                }

                return newData;
            }
            catch (IOException e) {
                throw new IllegalArgumentException("Illegal json string at: " + thePrefix + " - \n" + jsonStr, e);
            }
        });
    }

    /**
     * expand json string to data keys
     * @return new created pre-processor
     */
    public static Framework.PreProcessor expandJsonKeys() {
        return expandJsonKeys(null);
    }
    public static Framework.PreProcessor expandJsonKeys(String prefix) {
        return ((prefix1, data, options) -> {
            logger.debug("expanding json keys at {}", (prefix == null ? prefix1 : prefix));

            Map<String, String> data1 = expandJson(prefix).apply(prefix1, data, options);
            Map<String, String> data2 = expandListKeys(prefix).apply(prefix1, data1, options);
            return data2;
        });
    }

    /**
     * expand list of strings to data keys
     * @return new created pre-processor
     */
    public static Framework.PreProcessor expandListKeys() {
        return expandListKeys(null);
    }
    public static Framework.PreProcessor expandListKeys(String prefix) {
        return ((prefix1, data, options) -> {
            logger.debug("expanding list keys at {}", (prefix == null ? prefix1 : prefix));

            String thePrefix = prefix == null ? prefix1 : prefix;
            Pattern p = Pattern.compile("^" + Pattern.quote(thePrefix) + "\\[[\\d]+\\].*");
            return data.entrySet().stream()
                    .map(e -> {
                        if (p.matcher(e.getKey()).matches()) {
                            String newKey = isEmptyStr(thePrefix) ? e.getValue() : thePrefix + "." + e.getValue();
                            return entry(newKey, "true");
                        } else return e;
                    }).collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
            });
        }

    /**
     * change data key prefix from one to other
     * @param from from prefix
     * @param to to prefix
     * @return new created pre-processor
     */
    public static Framework.PreProcessor changePrefix(String from, String to) {
        return ((prefix, data, options) -> {
            logger.debug("changing prefix at {} from {} to {}", prefix, from, to);

            return data.entrySet().stream()
                .map(e -> {
                    if (!e.getKey().startsWith(prefix)) return e;
                    else {
                        String tail = e.getKey().substring(prefix.length())
                                .replaceFirst("^[\\.]?" + Pattern.quote(from), to)
                                .replaceFirst("^\\.", "");
                        String newKey = isEmptyStr(tail) ? prefix
                                : (prefix +"."+ tail).replaceFirst("^\\.", "");
                        return entry(
                            newKey,
                            e.getValue()
                        );
                    }
                }).collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
            });
        }

    /////////////////////////////////  pre-defined post err-processors  /////////////////////

    /**
     * fold errors of same key to one error list, e.g. (key, error1), (key, error2) -+ (key, (error1, error2))
     * @return new created function
     */
    public static Function<List<Map.Entry<String, String>>, Map<String, List<String>>>
                foldErrs() {
        return (errors) -> {
            logger.debug("folding errors");

            return errors.stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        HashMap::new,
                        Collectors.mapping(
                            Map.Entry::getValue,
                            Collectors.toList()
                        )
                ));
            };
        }

    /**
     * convert list of errors to tree of errors
     * @return new created function
     */
    public static Function<List<Map.Entry<String, String>>, Map<String, Object>>
                errsTree() {
        return ((errors) -> {
            logger.debug("converting errors list to errors tree");

            Map<String, Object> root = new HashMap<>();
            Map<String, Object> workList = mmap(entry("", root));
            for(Map.Entry<String, String> error : errors) {
                String name = error.getKey().replaceAll("\\[", ".").replaceAll("\\]", "");
                List<String> workObj = (List<String>) workObject(workList, name + "._errors", true);
                workObj.add(error.getValue());
            }
            return root;
        });
    }

    /////////////////////////////////  pre-defined touched checkers  /////////////////////

    /**
     * touched checker based on inputting touched list
     * @param touched the touched list
     * @return new created touched checker
     */
    public static Framework.TouchedChecker listTouched(List<String> touched) {
        return ((prefix, data) -> {
            logger.debug("checking touched in list for {}", prefix);

            return touched.stream()
                    .filter(key -> key.startsWith(prefix))
                    .count() > 0;
        });
    }

    /**
     * touched checker based touched records existed on data map
     * @param dataPrefix the data prefix
     * @param touchedPrefix the touched keys prefix
     * @return new created touched checker
     */
    public static Framework.TouchedChecker prefixTouched(String dataPrefix, String touchedPrefix) {
        return ((prefix, data) -> {
            logger.debug("checking touched with data prefix {} and touched prefix {} for {}", dataPrefix, touchedPrefix, prefix);

            String prefixToBeChecked = prefix.replaceAll("^" + Pattern.quote(dataPrefix), touchedPrefix);
            return data.keySet().stream()
                    .filter(key -> key.startsWith(prefixToBeChecked))
                    .count() > 0;
        });
    }
}
