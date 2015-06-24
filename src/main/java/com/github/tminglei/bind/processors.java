package com.github.tminglei.bind;

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
public interface processors {

    ///////////////////////////////////  pre-defined pre-processors  //////////////////////////

    default framework.PreProcessor trim() {
        return mkSimplePreProcessor(s -> s != null ? s.trim() : s);
    }

    default framework.PreProcessor omit(String str) {
        return mkSimplePreProcessor(s -> s != null ? s.replaceAll(str, "") : s);
    }

    default framework.PreProcessor omitLeft(String str) {
        return mkSimplePreProcessor(s -> s != null ? s.replaceAll("^" + Pattern.quote(str), "") : s);
    }

    default framework.PreProcessor omitRight(String str) {
        return mkSimplePreProcessor(s -> s != null ? s.replaceAll(Pattern.quote(str) + "$", "") : s);
    }

    default framework.PreProcessor omitRedundant(String str) {
        return mkSimplePreProcessor(s -> s != null ? s.replaceAll("["+str+"]+", str) : s);
    }

    default framework.PreProcessor omitWith(String pattern, String replacement) {
        return mkSimplePreProcessor(s -> s != null ? s.replaceAll(pattern, replacement) : s);
    }

    default framework.PreProcessor changePrefix(String from, String to) {
        return (prefix, data, options) -> {
            return data.entrySet().stream()
                .map(e -> new String[]{
                        e.getKey().replaceFirst("^" + from, to),
                        e.getValue()
                }).collect(Collectors.toMap(
                            e -> e[0],
                            e -> e[1]
                    ));
        };
    }

    /////////////////////////////////  pre-defined post err-processors  /////////////////////

    default Function<List<Map.Entry<String, String>>, Map<String, List<String>>>
                foldErrs() {
        return (errs) -> {
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

    default Function<List<Map.Entry<String, String>>, Map<String, Object>>
                errsTree() {
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
}
