package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanIntrospector;
import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;
import de.javaholic.toolkit.ui.annotations.UiHidden;
import de.javaholic.toolkit.ui.annotations.UiLabel;
import de.javaholic.toolkit.ui.annotations.UiOrder;
import de.javaholic.toolkit.ui.annotations.UiReadOnly;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Factory for UI semantic metadata.
 *
 * <p>Responsibility:</p>
 * <ul>
 * <li>Bridge technical introspection to UI metadata by wrapping {@link BeanMeta} into {@link UiMeta}</li>
 * <li>Evaluate UI semantic annotations for fields/getters</li>
 * </ul>
 *
 * <p>Must not do: render components, configure Grid/Form fields, or hold CRUD orchestration logic.</p>
 *
 * <p>Architecture fit: single entry point for UI metadata creation. Consumers use this to keep
 * {@code BeanMeta} technical-only and prevent direct reflection coupling in UI builders.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * UiMeta<User> meta = UiInspector.inspect(User.class);
 * meta.properties()
 *     .filter(UiProperty::isVisible)
 *     .forEach(p -> System.out.println(p.name()));
 * }</pre>
 */
public final class UiInspector {

    private UiInspector() {
    }

    /**
     * Inspects a type and returns the UI semantic metadata wrapper.
     *
     * <p>Example: {@code UiMeta<User> meta = UiInspector.inspect(User.class);}</p>
     */
    public static <T> UiMeta<T> inspect(Class<T> type) {
        Objects.requireNonNull(type, "type");
        BeanMeta<T> beanMeta = BeanIntrospector.inspect(type);
        List<UiProperty<T>> properties = new ArrayList<>();

        // ------------------------------------------------------------------
        // UI semantic annotation evaluation happens *only* in UiMeta.
        // Label resolution to actual display text happens *only* via TextResolver.
        // FieldRegistry consumes resolved attributes, but never evaluates annotations.
        // ------------------------------------------------------------------
        for (BeanProperty<T, ?> property : beanMeta.properties()) {
            Optional<AnnotatedElement> field = findField(type, property.name());
            Optional<AnnotatedElement> getter = findGetter(type, property.name());

            boolean defaultHidden = isTechnicalHidden(property, beanMeta);
            boolean hidden = findAnnotation(UiHidden.class, getter, field).isPresent() || defaultHidden;
            String labelKey = findAnnotation(UiLabel.class, getter, field)
                    .map(UiLabel::key)
                    .filter(key -> !key.isBlank())
                    .orElse(property.name());
            int order = findAnnotation(UiOrder.class, getter, field)
                    .map(UiOrder::value)
                    .orElse(Integer.MAX_VALUE);
            boolean readOnly = findAnnotation(UiReadOnly.class, getter, field).isPresent();

            properties.add(new UiProperty<>(beanMeta, property, !hidden, labelKey, order, readOnly));
        }

        return new UiMeta<>(beanMeta, properties);
    }

    private static <A extends java.lang.annotation.Annotation> Optional<A> findAnnotation(
            Class<A> annotationType,
            Optional<AnnotatedElement> primary,
            Optional<AnnotatedElement> secondary
    ) {
        if (primary.isPresent()) {
            A annotation = primary.get().getAnnotation(annotationType);
            if (annotation != null) {
                return Optional.of(annotation);
            }
        }
        if (secondary.isPresent()) {
            A annotation = secondary.get().getAnnotation(annotationType);
            if (annotation != null) {
                return Optional.of(annotation);
            }
        }
        return Optional.empty();
    }

    private static <T> boolean isTechnicalHidden(BeanProperty<T, ?> property, BeanMeta<T> beanMeta) {
        return beanMeta.idProperty().map(p -> p.name().equals(property.name())).orElse(false)
                || beanMeta.versionProperty().map(p -> p.name().equals(property.name())).orElse(false);
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
}
