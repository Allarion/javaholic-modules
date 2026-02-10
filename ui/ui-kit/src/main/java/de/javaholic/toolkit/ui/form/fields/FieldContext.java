package de.javaholic.toolkit.ui.form.fields;

import java.lang.reflect.AnnotatedElement;

public record FieldContext(
        Class<?> declaringType,
        String property,
        Class<?> fieldType,
        AnnotatedElement annotations
) {
}
