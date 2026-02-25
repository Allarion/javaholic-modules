package de.javaholic.toolkit.ui.form.fields;

import java.lang.reflect.AnnotatedElement;

public final class FieldContext {

    private final Class<?> declaringType;
    private final String property;
    private final Class<?> rawType;
    private final Class<?> elementType;
    private final AnnotatedElement annotations;

    public FieldContext(
            Class<?> declaringType,
            String property,
            Class<?> rawType,
            Class<?> elementType,
            AnnotatedElement annotations
    ) {
        this.declaringType = declaringType;
        this.property = property;
        this.rawType = rawType;
        this.elementType = elementType;
        this.annotations = annotations;
    }

    public Class<?> declaringType() {
        return declaringType;
    }

    public String property() {
        return property;
    }

    public Class<?> fieldType() {
        return rawType;
    }

    public AnnotatedElement annotations() {
        return annotations;
    }

    public Class<?> getRawType() {
        return rawType;
    }

    public Class<?> getElementType() {
        return elementType;
    }
}
