package de.javaholic.toolkit.ui.meta;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Optional;

final class PropertyElements {

    private final AnnotatedElement field;
    private final AnnotatedElement getter;
    private final AnnotatedElement recordComponent;

    private PropertyElements(
           AnnotatedElement field,
           AnnotatedElement getter,
           AnnotatedElement recordComponent
    ) {
        this.field = field;
        this.getter = getter;
        this.recordComponent = recordComponent;
    }

    static PropertyElements resolve(Class<?> type, String propertyName) {
        return new PropertyElements(
                findField(type, propertyName).orElse(null),
                findGetter(type, propertyName).orElse(null),
                findRecordComponent(type, propertyName).orElse(null)
        );
    }

    private static Optional<AnnotatedElement> findField(Class<?> type, String propertyName) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(propertyName);
                return Optional.of(field);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return Optional.empty();
    }

    private static Optional<AnnotatedElement> findGetter(Class<?> type, String propertyName) {
        String suffix = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        String getName = "get" + suffix;
        String isName = "is" + suffix;
        for (Method method : type.getMethods()) {
            if (method.getParameterCount() != 0) {
                continue;
            }
            if (method.getDeclaringClass() == Object.class) {
                continue;
            }
            if (method.getName().equals(getName) || method.getName().equals(isName) || method.getName().equals(propertyName)) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    private static Optional<AnnotatedElement> findRecordComponent(Class<?> type, String propertyName) {
        if (!type.isRecord()) {
            return Optional.empty();
        }
        for (RecordComponent component : type.getRecordComponents()) {
            if (component.getName().equals(propertyName)) {
                return Optional.of(component);
            }
        }
        return Optional.empty();
    }

    public  Optional<AnnotatedElement> getField() {
        return Optional.ofNullable(field);
    }

    public  Optional<AnnotatedElement> getGetter() {
        return Optional.ofNullable(getter);
    }

    public  Optional<AnnotatedElement> getRecordComponent() {
        return Optional.ofNullable(recordComponent);
    }
}
