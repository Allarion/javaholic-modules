package de.javaholic.toolkit.introspection;

import java.lang.reflect.AnnotatedElement;

public record BeanProperty(
        String name,
        Class<?> type,
        AnnotatedElement definition
) {
}
