package de.javaholic.util.ui.i18n;

import jakarta.validation.ConstraintViolation;

import java.lang.annotation.Annotation;

/**
 * Generates validation keys based on annotation class names.
 *
 * <pre>{@code
 * String key = ValidationKeys.field(NotNull.class); // validation.field.NotNull
 * }</pre>
 */
public final class ValidationKeys {

    private ValidationKeys() {
    }

    public static String field(Class<? extends Annotation> annotationType) {
        return "validation.field." + annotationType.getSimpleName();
    }

    public static String field(Annotation annotation) {
        return field(annotation.annotationType());
    }

    public static String field(ConstraintViolation<?> violation) {
        return field(violation.getConstraintDescriptor().getAnnotation());
    }
}
