package com.github.tminglei.bind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tminglei on 6/6/15.
 */
public class Helpers {

    public static PrependHelper $p(framework.Constraint... constraints) {
        return new PrependHelper().then(constraints);
    }
    public static PrependHelper $p(framework.PreProcessor... processors) {
        return new PrependHelper().pipe(processors);
    }

    /////////////////////////////////////////////////////////////////
    static class PrependHelper {
        private List<framework.Constraint> _constraints
                = new ArrayList<>();
        private List<framework.PreProcessor> _processors
                = new ArrayList<>();

        PrependHelper() {}

        public PrependHelper then(framework.Constraint... constraints) {
            _constraints.addAll(Arrays.asList(constraints));
            return this;
        }
        public PrependHelper pipe(framework.PreProcessor... processors) {
            _processors.addAll(Arrays.asList(processors));
            return this;
        }
        public <T> framework.Mapping<T> to(framework.Mapping<T> mapping) {
            framework.Mapping<T> mapping1 = mapping.options(o -> Options.setter(o)
                            .prepend_constraints(
                                    _constraints.toArray(new framework.Constraint[0]))
                            .get());
            framework.Mapping<T> mapping2 = mapping1.options(o -> Options.setter(o)
                            .prepend_processors(
                                    _processors.toArray(new framework.PreProcessor[0]))
                            .get());
            return mapping2;
        }
    }
}
