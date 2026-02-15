package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;
import de.javaholic.toolkit.ui.annotations.UIRequired;
import de.javaholic.toolkit.ui.annotations.UiHidden;
import de.javaholic.toolkit.ui.annotations.UiLabel;
import de.javaholic.toolkit.ui.annotations.UiOrder;
import de.javaholic.toolkit.ui.annotations.UiPermission;
import de.javaholic.toolkit.ui.annotations.UiReadOnly;
import jakarta.persistence.Column;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

final class UiPropertyInterpreter {

    <T> UiProperty<T> interpret(
            Class<T> type,
            BeanProperty<T, ?> property,
            BeanMeta<T> beanMeta,
            BeanKind beanKind
    ) {
        PropertyElements elements = PropertyElements.resolve(type, property.name());

        boolean technical = isTechnical(property, beanMeta);
        boolean hidden = findAnnotation(UiHidden.class, elements).isPresent();
        String permissionKey = findAnnotation(UiPermission.class, elements)
                .map(UiPermission::value)
                .filter(value -> !value.isBlank())
                .orElse(null);
        boolean requiredByColumn = isRequiredByColumn(beanKind, elements);
        boolean requiredByUiAnnotation = findAnnotation(UIRequired.class, elements).isPresent();
        boolean required = requiredByColumn || requiredByUiAnnotation;
        String labelKey = findAnnotation(UiLabel.class, elements)
                .map(UiLabel::value)
                .filter(key -> !key.isBlank())
                .orElse(property.name());
        int order = findAnnotation(UiOrder.class, elements)
                .map(UiOrder::value)
                .orElse(Integer.MAX_VALUE);
        boolean readOnly = findAnnotation(UiReadOnly.class, elements).isPresent();

        return new UiProperty<>(
                beanMeta,
                property,
                hidden,
                technical,
                required,
                permissionKey,
                labelKey,
                order,
                readOnly
        );
    }

    private static boolean isRequiredByColumn(BeanKind beanKind, PropertyElements elements) {
        if (beanKind != BeanKind.JPA_ENTITY) {
            return false;
        }
        return findAnnotation(Column.class, elements)
                .map(column -> !column.nullable())
                .orElse(false);
    }

    private static <T> boolean isTechnical(BeanProperty<T, ?> property, BeanMeta<T> beanMeta) {
        return beanMeta.idProperty().map(p -> p.name().equals(property.name())).orElse(false)
                || beanMeta.versionProperty().map(p -> p.name().equals(property.name())).orElse(false);
    }

    private static <A extends java.lang.annotation.Annotation> Optional<A> findAnnotation(
            Class<A> annotationType,
            PropertyElements elements
    ) {
        if (elements.getter.isPresent()) {
            A annotation = elements.getter.get().getAnnotation(annotationType);
            if (annotation != null) {
                return Optional.of(annotation);
            }
        }
        if (elements.field.isPresent()) {
            A annotation = elements.field.get().getAnnotation(annotationType);
            if (annotation != null) {
                return Optional.of(annotation);
            }
        }
        if (elements.recordComponent.isPresent()) {
            A annotation = elements.recordComponent.get().getAnnotation(annotationType);
            if (annotation != null) {
                return Optional.of(annotation);
            }
        }
        return Optional.empty();
    }
}
