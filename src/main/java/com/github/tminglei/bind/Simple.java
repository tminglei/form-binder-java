package com.github.tminglei.bind;

import java.util.*;

import com.github.tminglei.bind.spi.*;

/**
 * helper class to easy usage
 */
public class Simple extends Framework {

    /**
     * create data map from a map (name -* value array) and other params
     * @param params params of type of Map(String -* String[])
     * @param others other params
     * @return new created data map
     */
    public static Map<String, String> data(Map<String, String[]> params, Map.Entry<String, String>... others) {
        Map<String, String> result = new HashMap<>();
        // expand params
        for(Map.Entry<String, String[]> entry : params.entrySet()) {
            if (entry.getValue() == null || entry.getValue().length == 0)
                continue;
            else if (entry.getValue().length == 1 && ! entry.getKey().endsWith("[]"))
                result.put(entry.getKey(), entry.getValue()[0]);
            else {
                for(int i = 0; i < entry.getValue().length; i++) {
                    String key = entry.getKey().replaceAll("\\[\\]$", "") + "[" + i + "]";
                    result.put(key, entry.getValue()[i]);
                }
            }
        }
        // merge in others
        for(Map.Entry<String, String> entry : others) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * shortcut method to construct group mapping
     * @param fields fields for the group mapping
     * @return new created mapping
     */
    public static GroupMapping mapping(Map.Entry<String, Mapping<?>>... fields) {
        return new GroupMapping(Arrays.asList(fields));
    }

    /**
     * helper method to construct group field mapping
     * @param name field name (short name)
     * @param mapping the related mapping
     * @return field entry
     */
    public static Map.Entry<String, Mapping<?>> field(String name, Mapping<?> mapping) {
        return FrameworkUtils.entry(name, mapping);
    }

    /**
     * help method to initialize a Attaching, which was used to
     * attach some constraints w/ or w/o some pre-processors to a mapping
     * @param constraints constraints
     * @return new created Attaching
     */
    public static Attaching attach(Constraint... constraints) {
        return new Attaching(Arrays.asList(constraints), null);
    }

    /**
     * help method to initialize a Attaching, which was used to
     * attach some pre-processors w/ or w/o some constraints to a mapping
     * @param processors pre-processors
     * @return new created Attaching
     */
    public static Attaching attach(PreProcessor... processors) {
        return new Attaching(null, Arrays.asList(processors));
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * used to attach constraints/pre-processors to a mapping
     */
    public static class Attaching {
        private List<Constraint> constraints
                = new ArrayList<>();
        private List<PreProcessor> processors
                = new ArrayList<>();

        Attaching(List<Constraint> constraints, List<PreProcessor> processors) {
            this.constraints = constraints != null ? constraints : new ArrayList<>();
            this.processors = processors != null ? processors : new ArrayList<>();
        }

        /**
         * mix in some constraints
         * @param constraints constraints
         * @return the Attaching
         */
        public Attaching then(Constraint... constraints) {
            this.constraints.addAll(Arrays.asList(constraints));
            return this;
        }

        /**
         * pipe in some pre-processors
         * @param processors pre-processors
         * @return the Attaching
         */
        public Attaching pipe(PreProcessor... processors) {
            this.processors.addAll(Arrays.asList(processors));
            return this;
        }

        /**
         * attach to target mapping
         * @param mapping target mapping
         * @param <T> base type
         * @return result mapping
         */
        public <T> Mapping<T> to(Mapping<T> mapping) {
            Mapping<T> mapping1 = mapping.options(o -> o.prepend_constraints(constraints));
            Mapping<T> mapping2 = mapping1.options(o -> o.prepend_processors(processors));
            return mapping2;
        }
    }
}
