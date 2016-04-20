package com.github.tminglei.bind;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Reflect/property utility methods
 */
public class PropertyUtils {

    public static final String PROPERTY_NAME_IS_NULL = "Property name is NULL!";
    public static final String CAN_T_FIND_PROPERTY_1$S_IN_CLASS_2$S = "Can't find property '%1$s' in class %2$s";
    private static Map<String,Map<String,PropertyDescriptor>> pdCache =
            Collections.synchronizedMap( new WeakHashMap<>() );

    public static Object readProperty( Object bean, String propName ) {
        Objects.requireNonNull(bean, "Bean object is NULL!");
        Objects.requireNonNull(propName, PROPERTY_NAME_IS_NULL);

        PropertyDescriptor pd = findPropertyDescriptor( bean.getClass(), propName );

        if( pd == null)
            throw new IllegalArgumentException( String.format(CAN_T_FIND_PROPERTY_1$S_IN_CLASS_2$S,
                    propName, bean.getClass().getName()));

        try {
            Method method = pd.getReadMethod();

            if (method == null)
                throw new UnsupportedOperationException("Property '" + pd.getName() + "' is not readable");

            return method.invoke(bean, new Object[0]);
        }
        catch (Exception e) {
            throw new RuntimeException( String.format("Exception occurred when reading property '%1$s': %2$s",
                    pd.getName(), e.getMessage() ) );
        }
    }

    public static void writeProperty( Object bean, String propName, Object propValue ) {
        Objects.requireNonNull(bean, "Bean object is NULL!");
        Objects.requireNonNull(propName, PROPERTY_NAME_IS_NULL);

        PropertyDescriptor pd = findPropertyDescriptor( bean.getClass(), propName );

        if( pd == null)
            throw new IllegalArgumentException( String.format(CAN_T_FIND_PROPERTY_1$S_IN_CLASS_2$S,
                    propName, bean.getClass().getName()));

        try {
            Method method = pd.getWriteMethod();

            if (method == null)
                throw new UnsupportedOperationException("Property '" + pd.getName() + "' is not writeable");

            method.invoke( bean, new Object[] { propValue } );
        }
        catch (Exception e) {
            throw new RuntimeException( String.format("Exception occurred when writing property '%1$s': %2$s",
                    pd.getName(), e.getMessage() ) );
        }
    }

    public static Class<?> getPropertyType( Class<?> beanclazz, String propName ) {
        Objects.requireNonNull(beanclazz, "Bean class is NULL!");
        Objects.requireNonNull(propName, PROPERTY_NAME_IS_NULL);

        PropertyDescriptor pd = findPropertyDescriptor( beanclazz, propName );

        if( pd == null)
            throw new IllegalArgumentException( String.format(CAN_T_FIND_PROPERTY_1$S_IN_CLASS_2$S,
                    propName, beanclazz.getName()));

        return pd.getPropertyType();
    }

    static PropertyDescriptor findPropertyDescriptor( Class<?> beanclazz, String propName ) {
        Map<String,PropertyDescriptor> pdmap = introspect( beanclazz );

        PropertyDescriptor pd = pdmap.get(propName);
        if( pd == null)
            pd = pdmap.get( propName.substring(0, 1).toLowerCase() + propName.substring(1));
        if( pd == null)
            pd = pdmap.get( propName.substring(0, 1).toUpperCase() + propName.substring(1));

        return pd;
    }

    //////

    public static Class<?>[] getGenericParamTypes( Type genericType ) {
        Objects.requireNonNull(genericType, "Generic type is NULL!");

        try {
            if( genericType instanceof ParameterizedType ) {
                Type[] genericTypes = ((ParameterizedType)genericType) .getActualTypeArguments();
                Class<?>[] result = new Class<?>[ genericTypes.length ];

                for( int i = 0; i < genericTypes.length; i++ ) {
                    if( genericTypes[i] instanceof Class<?> ) {
                        result[i] = (Class<?>) genericTypes[i];
                    }
                    else if( genericTypes[i] instanceof ParameterizedType ) {
                        Type rawType = ((ParameterizedType) genericTypes[i]) .getRawType();
                        result[i] = (Class<?>) rawType;
                    }
                    else {
                        // ignore it
                    }
                }

                return result;
            }
        }
        catch (Exception e) {
            ; // ignore it
        }

        return new Class<?>[0];
    }

    public static Class<?> getReturnType( Class<?> declaringClass, String methodName, Class<?>... parameterTypes ) {
        Objects.requireNonNull(declaringClass, "Declaring class is NULL!");
        Objects.requireNonNull(methodName, "Method name is NULL!");

        try {
            Method method = declaringClass.getDeclaredMethod( methodName, parameterTypes );
            return method.getReturnType();
        }
        catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Can't find method of %1$s(%2$s) in %3$s",
                            methodName, Arrays.asList(parameterTypes), declaringClass ) );
        }
    }

    //---------------------------------------------------- inner support methods ---


    static Map<String,PropertyDescriptor> introspect( Class<?> beanclazz ) {
        Map<String,PropertyDescriptor> pdmap = pdCache.get( beanclazz.getName() );

        if ( pdmap == null ) {
            PropertyDescriptor[] pds = null;

            try {
                pds = Introspector.getBeanInfo(beanclazz)
                        .getPropertyDescriptors();
            }
            catch (IntrospectionException e) {
                throw new RuntimeException(e);  // Should never happen
            }

            pdmap = new HashMap<>();

            for( int i = 0; i < pds.length; i++ ) {
                // skip Object.getClass() and Map/Collection.isEmpty()
                if( pds[i].getName().equals("class")
                        || pds[i].getName().equals("empty") )
                    continue;

                pdmap.put( pds[i].getName(), pds[i] );
            }

            pdCache.put( beanclazz.getName(), pdmap );
        }

        return pdmap;
    }

}
