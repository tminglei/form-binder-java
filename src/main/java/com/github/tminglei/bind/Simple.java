package com.github.tminglei.bind;

import java.util.*;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * helper class to easy usage
 */
public class Simple extends Framework {

    public static Mapping<BindObject> mapping(Map.Entry<String, Mapping<?>>... fields) {
        return new GroupMapping(Arrays.asList(fields));
    }
    public static Map.Entry<String, Mapping<?>> field(String name, Mapping<?> mapping) {
        return FrameworkUtils.entry(name, mapping);
    }

    public static Binding   $(String name) {
        return new Binding(name);
    }
    public static Attaching $(Framework.Constraint... constraints) {
        return new Attaching(Arrays.asList(constraints), null);
    }
    public static Attaching $(Framework.PreProcessor... processors) {
        return new Attaching(null, Arrays.asList(processors));
    }

    /////////////////////////////////////////////////////////////////
    private static <T> Framework.Mapping<T> //(prepend) attach constraints and pre-processors to a mapping
                attach(Framework.Mapping<T> mapping, List<Framework.Constraint> constraints, List<Framework.PreProcessor> processors) {
        Framework.Mapping<T> mapping1 = mapping.options(o ->
                o.prepend_constraints(constraints.toArray(new Framework.Constraint[0])));
        Framework.Mapping<T> mapping2 = mapping1.options(o ->
                o.prepend_processors(processors.toArray(new Framework.PreProcessor[0])));
        return mapping2;
    }

    /**
     * used to bind a field name to a mapping w/ or w/o constraints/pre-processors
     */
    public static class Binding {
        private String name;
        private List<Framework.Constraint> constraints
                = new ArrayList<>();
        private List<Framework.PreProcessor> processors
                = new ArrayList<>();

        Binding(String name) {
            this.name = name;
        }

        /**
         * mix in some constraints
         * @param constraints
         * @return
         */
        public Binding then(Framework.Constraint... constraints) {
            this.constraints.addAll(Arrays.asList(constraints));
            return this;
        }

        /**
         * pipe in some pre-processors
         * @param processors
         * @return
         */
        public Binding pipe(Framework.PreProcessor... processors) {
            this.processors.addAll(Arrays.asList(processors));
            return this;
        }

        /**
         * bind to target mapping
         * @param mapping
         * @return
         */
        public Map.Entry<String, Framework.Mapping<?>> to(Framework.Mapping<?> mapping) {
            return entry(name, attach(mapping, constraints, processors));
        }
    }

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
         * @param constraints
         * @return
         */
        public Attaching then(Framework.Constraint... constraints) {
            this.constraints.addAll(Arrays.asList(constraints));
            return this;
        }

        /**
         * pipe in some pre-processors
         * @param processors
         * @return
         */
        public Attaching pipe(Framework.PreProcessor... processors) {
            this.processors.addAll(Arrays.asList(processors));
            return this;
        }

        /**
         * attach to target mapping
         * @param mapping
         * @param <T>
         * @return
         */
        public <T> Framework.Mapping<T> to(Framework.Mapping<T> mapping) {
            return attach(mapping, constraints, processors);
        }
    }
}
