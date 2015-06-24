package com.github.tminglei.bind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * helper class to easy usage
 */
public class Simple implements mappings, constraints, processors {

    public static Binding $(String name) {
        return new Binding(name);
    }
    public static Attaching $(framework.Constraint... constraints) {
        return new Attaching(Arrays.asList(constraints), null);
    }
    public static Attaching $(framework.PreProcessor... processors) {
        return new Attaching(null, Arrays.asList(processors));
    }

    /////////////////////////////////////////////////////////////////
    private static <T> framework.Mapping<T> //(prepend) attach constraints and pre-processors to a mapping
                attach(framework.Mapping<T> mapping, List<framework.Constraint> constraints, List<framework.PreProcessor> processors) {
        framework.Mapping<T> mapping1 = mapping.options(o ->
                o.prepend_constraints(constraints.toArray(new framework.Constraint[0])));
        framework.Mapping<T> mapping2 = mapping1.options(o ->
                o.prepend_processors(processors.toArray(new framework.PreProcessor[0])));
        return mapping2;
    }

    static class Binding {
        private String name;
        private List<framework.Constraint> constraints
                = new ArrayList<>();
        private List<framework.PreProcessor> processors
                = new ArrayList<>();

        Binding(String name) {
            this.name = name;
        }

        public Binding then(framework.Constraint... constraints) {
            this.constraints.addAll(Arrays.asList(constraints));
            return this;
        }
        public Binding pipe(framework.PreProcessor... processors) {
            this.processors.addAll(Arrays.asList(processors));
            return this;
        }

        public <T> Map.Entry<String, framework.Mapping<T>> to(framework.Mapping<T> mapping) {
            return entry(name, attach(mapping, constraints, processors));
        }
    }

    static class Attaching {
        private List<framework.Constraint> constraints
                = new ArrayList<>();
        private List<framework.PreProcessor> processors
                = new ArrayList<>();

        Attaching(List<framework.Constraint> constraints, List<framework.PreProcessor> processors) {
            this.constraints = constraints != null ? constraints : new ArrayList<>();
            this.processors = processors != null ? processors : new ArrayList<>();
        }

        public Attaching then(framework.Constraint... constraints) {
            this.constraints.addAll(Arrays.asList(constraints));
            return this;
        }
        public Attaching pipe(framework.PreProcessor... processors) {
            this.processors.addAll(Arrays.asList(processors));
            return this;
        }

        public <T> framework.Mapping<T> to(framework.Mapping<T> mapping) {
            return attach(mapping, constraints, processors);
        }
    }
}
