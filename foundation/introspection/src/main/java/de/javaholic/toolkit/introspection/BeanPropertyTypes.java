package de.javaholic.toolkit.introspection;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public final class BeanPropertyTypes {

    private BeanPropertyTypes() {
    }

    public static Class<?> resolveCollectionElementType(Class<?> owningType, BeanProperty<?, ?> beanProperty) {
        if (!Collection.class.isAssignableFrom(beanProperty.type())) {
            return null;
        }
        Method setter = findSetter(owningType, beanProperty.name());
        if (setter == null) {
            return null;
        }
        Type genericType = setter.getGenericParameterTypes()[0];
        if (!(genericType instanceof ParameterizedType parameterizedType)) {
            return null;
        }
        Type typeArg = parameterizedType.getActualTypeArguments()[0];
        if (typeArg instanceof Class<?> clazz) {
            return clazz;
        }
        if (typeArg instanceof ParameterizedType nested && nested.getRawType() instanceof Class<?> rawClass) {
            return rawClass;
        }
        return Object.class;
    }

    private static Method findSetter(Class<?> owningType, String propertyName) {
        try {
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(owningType).getPropertyDescriptors()) {
                if (propertyName.equals(descriptor.getName())) {
                    return descriptor.getWriteMethod();
                }
            }
        } catch (IntrospectionException ignored) {
            // element type detection is optional
        }
        return null;
    }
}
