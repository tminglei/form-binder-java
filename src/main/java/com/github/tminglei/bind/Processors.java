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
    private static final Logger log = LoggerFactory.getLogger(Processors.class);

    ///////////////////////////////////  pre-defined pre-processors  //////////////////////////

    public static Framework.PreProcessor trim() {
        return ((prefix, data, options) -> {
            log.debug("trimming '{}'", prefix);

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
            log.debug("replacing '{}' with '{}'", pattern, replacement);

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

    public static Framework.PreProcessor expandJson() {
        return expandJson(null);
    }
    public static Framework.PreProcessor expandJson(String prefix) {
        return ((prefix1, data, options) -> {
            log.debug("expanding json at {}", (prefix == null ? prefix1 : prefix));

            String thePrefix = prefix == null ? prefix1 : prefix;
            String jsonStr  = data.get(thePrefix);

            Map<String, String> newData = new HashMap<>(data);
            newData.remove(thePrefix); // remove old one to avoid disturbing other processing

            try {
                if (isEmptyStr(jsonStr)) {
                    log.warn("json string is '{}'", jsonStr);
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

    public static Framework.PreProcessor expandJsonKeys() {
        return expandJsonKeys(null);
    }
    public static Framework.PreProcessor expandJsonKeys(String prefix) {
        return ((prefix1, data, options) -> {
            log.debug("expanding json keys at {}", (prefix == null ? prefix1 : prefix));

            String thePrefix = prefix == null ? prefix1 : prefix;
            String jsonStr  = data.get(thePrefix);

            Map<String, String> newData = new HashMap<>(data);
            newData.remove(thePrefix); // remove old one to avoid disturbing other processing

            try {
                if (isEmptyStr(jsonStr)) {
                    log.warn("json string is '{}'", jsonStr);
                } else {
                    JsonNode json = new ObjectMapper().readTree(jsonStr);
                    if (!json.isArray() || (json.size() > 0 && !json.get(0).isTextual())) {
                        throw new IllegalArgumentException(thePrefix + " is NOT AN String ARRAY!");
                    }

                    for(JsonNode key : json) {
                        String newKey = isEmptyStr(thePrefix) ? key.asText() : thePrefix + "." + key.asText();
                        newData.put(newKey, "true");
                    }
                }

                return newData;
            }
            catch (IOException e) {
                throw new IllegalArgumentException("Illegal json string at " + thePrefix + " - \n" + jsonStr, e);
            }
        });
    }

    public static Framework.PreProcessor changePrefix(String from, String to) {
        return ((prefix, data, options) -> {
            log.debug("changing prefix at {} from {} to {}", prefix, from, to);

            return data.entrySet().stream()
                .map(e -> {
                    if (!e.getKey().startsWith(prefix)) return e;
                    else {
                        String toBeReplaced = e.getKey().substring(prefix.length());
                        String newKey = (prefix + toBeReplaced.replaceFirst("^" + Pattern.quote(from), to))
                                .replaceFirst("^\\.", "");
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

    public static Function<List<Map.Entry<String, String>>, Map<String, List<String>>>
                foldErrs() {
        return (errs) -> {
            log.debug("folding errors");

            return errs.stream()
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

    public static Function<List<Map.Entry<String, String>>, Map<String, Object>>
                errsTree() {
        log.debug("converting errors list to errors tree");

        return ((errors) -> {
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

    public static Framework.TouchedChecker listTouched(List<String> touched) {
        return ((prefix, data) -> {
            log.debug("checking touched in list for {}", prefix);

            return touched.stream()
                    .filter(key -> key.startsWith(prefix))
                    .count() > 0;
        });
    }

    public static Framework.TouchedChecker prefixTouched(String dataPrefix, String touchedPrefix) {
        return ((prefix, data) -> {
            log.debug("checking touched with data prefix {} and touched prefix {} for {}", dataPrefix, touchedPrefix, prefix);

            String prefixToBeChecked = prefix.replaceAll("^" + Pattern.quote(dataPrefix), touchedPrefix);
            return data.keySet().stream()
                    .filter(key -> key.startsWith(prefixToBeChecked))
                    .count() > 0;
        });
    }
}
