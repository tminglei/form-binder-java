package com.github.tminglei.bind;

import java.util.*;

/**
 * helper class to easy usage
 */
public class Simple extends Framework {

    /**
     * shortcut method to construct group mapping
     * @param fields fields for the group mapping
     * @return new created mapping
     */
    public static Mapping<BindObject> mapping(Map.Entry<String, Mapping<?>>... fields) {
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
    public static Attaching attach(Framework.Constraint... constraints) {
        return new Attaching(Arrays.asList(constraints), null);
    }

    /**
     * help method to initialize a Attaching, which was used to
     * attach some pre-processors w/ or w/o some constraints to a mapping
     * @param processors pre-processors
     * @return new created Attaching
     */
    public static Attaching attach(Framework.PreProcessor... processors) {
        return new Attaching(null, Arrays.asList(processors));
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * used to attach constraints/pre-processors to a mapping
     */
    public static class Attaching {
        private List<Framework.Constraint> constraints
                = new ArrayList<>();
        private List<Framework.PreProcessor> processors
                = new ArrayList<>();

        Attaching(List<Framework.Constraint> constraints, List<Framework.PreProcessor> processors) {
            this.constraints = constraints != null ? constraints : new ArrayList<>();
            this.processors = processors != null ? processors : new ArrayList<>();
        }

        /**
         * mix in some constraints
         * @param constraints constraints
         * @return the Attaching
         */
        public Attaching then(Framework.Constraint... constraints) {
            this.constraints.addAll(Arrays.asList(constraints));
            return this;
        }

        /**
         * pipe in some pre-processors
         * @param processors pre-processors
         * @return the Attaching
         */
        public Attaching pipe(Framework.PreProcessor... processors) {
            this.processors.addAll(Arrays.asList(processors));
            return this;
        }

        /**
         * attach to target mapping
         * @param mapping target mapping
         * @param <T> base type
         * @return result mapping
         */
        public <T> Framework.Mapping<T> to(Framework.Mapping<T> mapping) {
            Framework.Mapping<T> mapping1 = mapping.options(o ->
                    o.prepend_constraints(constraints.toArray(new Framework.Constraint[0])));
            Framework.Mapping<T> mapping2 = mapping1.options(o ->
                    o.prepend_processors(processors.toArray(new Framework.PreProcessor[0])));
            return mapping2;
        }
    }
}
