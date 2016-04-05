package com.github.tminglei.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.tminglei.bind.FrameworkUtils.*;

/**
 * used to hold/fetch/create transformers
 */
public class Transformers {
    static final Logger logger = LoggerFactory.getLogger(Transformers.class);

    static final Registry REGISTRY = new Registry();
    static {
        register(Byte.class, Byte.TYPE, (Function<Byte, Byte>) FrameworkUtils.PASS_THROUGH);
        register(Short.class, Short.TYPE, (Function<Short, Short>) FrameworkUtils.PASS_THROUGH);
        register(Integer.class, Integer.TYPE, (Function<Integer, Integer>) FrameworkUtils.PASS_THROUGH);
        register(Long.class, Long.TYPE, (Function<Long, Long>) FrameworkUtils.PASS_THROUGH);
        register(Float.class, Float.TYPE, (Function<Float, Float>) FrameworkUtils.PASS_THROUGH);
        register(Double.class, Double.TYPE, (Function<Double, Double>) FrameworkUtils.PASS_THROUGH);
        register(Boolean.class, Boolean.TYPE, (Function<Boolean, Boolean>) FrameworkUtils.PASS_THROUGH);
        register(Character.class, Character.TYPE, (Function<Character, Character>) FrameworkUtils.PASS_THROUGH);
    }

    /**
     * register a transform function for from type -* to type
     *
     * @param from class of from type
     * @param to   class of to type
     * @param transform transform function
     * @param <T>  from type
     * @param <R>  to type
     */
    public static <T,R> void register(Class<T> from, Class<R> to, Function<T,R> transform) {
        REGISTRY.register(from, to, transform);
    }

    /**
     * fetch a transform function for from type -* to type
     *
     * @param from class of from type
     * @param to   class of to type
     * @param <T>  from type
     * @param <R>  to type
     * @return transform function
     * @throws IllegalArgumentException if a suitable transform function not found
     */
    public static  <T,R> Function<T,R> transform(Class<T> from, Class<R> to) {
        return REGISTRY.transformFor(from, to);
    }

    ///---

    /**
     * create a transform function for BindObject -* [bean] type
     *
     * @param beanClazz bean class
     * @param <R>       bean type
     * @return new created transform function
     */
    public static <R> Function<BindObject, R> transTo(Class<R> beanClazz) {
        Objects.requireNonNull(beanClazz, "beanClazz is NULL!!");
        return (bindObj) -> transform(bindObj, beanClazz, REGISTRY);
    }

    public static <R> Function<BindObject, R> transTo(Class<R> beanClazz, Registry more) {
        Objects.requireNonNull(beanClazz, "beanClazz is NULL!!");
        return (bindObj) -> transform(bindObj, beanClazz, REGISTRY.merge(more));
    }

    ///---

    static <T> T transform(Object value, Class<T> toClazz, Registry registry) {
        try {
            if (value instanceof BindObject) {
                T bean = newInstance(toClazz);

                for(Map.Entry<String, Object> entry : (BindObject) value) {
                    Class<?> requiredType = PropertyUtils.getPropertyType(toClazz, entry.getKey());
                    Object propValue = transform(entry.getValue(), requiredType, registry);

                    if (propValue != null) {
                        PropertyUtils.writeProperty(bean, entry.getKey(), propValue);
                    }
                }

                return bean;
            }
            else if (value instanceof Optional) {
                Optional<?> optional = (Optional) value;

                if (toClazz == Optional.class) {
                    Class<?> targetType = PropertyUtils.getGenericParamTypes(toClazz)[0];
                    return (T) optional.map(v -> transform(v, targetType, registry));
                } else {
                    return transform(optional.orElse(null), toClazz, registry);
                }
            }
            else if (value instanceof Map) {
                Map<?, ?> values = (Map) value;

                if (Map.class.isAssignableFrom(toClazz)) {
                    Class<?> keyType = PropertyUtils.getGenericParamTypes(toClazz)[0];
                    Class<?> valueType = PropertyUtils.getGenericParamTypes(toClazz)[1];

                    values = values.entrySet().stream().map(e ->
                        entry(transform(e.getKey(), keyType, registry),
                              transform(e.getValue(), valueType, registry))
                    ).collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));

                    return doTransform(values, toClazz, registry);
                }
                else throw new IllegalArgumentException(
                        "INCOMPATIBLE transform: " + value.getClass().getName() + " -> " + toClazz.getName());
            }
            else if (value instanceof Collection) {
                Collection<?> values = (Collection) value;

                if (Collection.class.isAssignableFrom(toClazz) || toClazz.isArray()) {
                    Class<?> elemType = toClazz.isArray() ? toClazz.getComponentType()
                            : PropertyUtils.getGenericParamTypes(toClazz)[0];

                    values = values.stream().map(v -> transform(v, elemType, registry))
                            .collect(Collectors.<Object>toList());

                    return doTransform(values, toClazz, registry);
                }
                else throw new IllegalArgumentException(
                        "INCOMPATIBLE transform: " + value.getClass().getName() + " -> " + toClazz.getName());
            }
            else {
                return doTransform(value, toClazz, registry);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <R> R doTransform(Object value, Class<R> toClazz, Registry registry) {
        if (value == null) return null;
        else {
            Function<Object, R> transformer = registry.transformFor((Class) value.getClass(), toClazz);
            return transformer.apply(value);
        }
    }

    static <T> T newInstance(Class<T> beanClazz) {
        try {
            return beanClazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    /**
     * a register class, used to hold/find transformers
     */
    public static class Registry {
        private final Map<Class<?>, Map.Entry<Class<?>, Function<?, ?>>> transformers =
                new ConcurrentHashMap<>();

        public <T,R> void register(Class<T> from, Class<R> to, Function<T,R> transform) {
            Objects.requireNonNull(from, "from is NULL!!");
            Objects.requireNonNull(to, "to is NULL!!");
            Objects.requireNonNull(transform, "transform is NULL!!");

            logger.info("registering transformer for {} -> {}", from.getName(), to.getName());
            transformers.putIfAbsent(from, new AbstractMap.SimpleImmutableEntry<>(to, transform));
        }

        public <T,R> Function<T,R> transformFor(Class<T> from, Class<R> to) {
            Objects.requireNonNull(from, "from is NULL!!");
            Objects.requireNonNull(to, "to is NULL!!");

            if (to.isAssignableFrom(from)) return (Function<T,R>) PASS_THROUGH;
            else {
                Map.Entry<Class<?>, Function<?, ?>> entry;

                Class<?> superClazz = from;
                while (superClazz != null) {
                    entry = transformers.get(superClazz);
                    if (entry != null && to.isAssignableFrom(entry.getKey())) {
                        return (Function<T,R>) entry.getValue();
                    }
                    superClazz = superClazz.getSuperclass();
                }
            }

            throw new IllegalArgumentException(
                    "CAN'R find transformer for " + from.getName() + " -> " + to.getName());
        }

        ///--

        public Registry merge(Registry other) {
            Registry merged = new Registry();
            merged.transformers.putAll(this.transformers);
            merged.transformers.putAll(other.transformers);
            return merged;
        }
    }
}
