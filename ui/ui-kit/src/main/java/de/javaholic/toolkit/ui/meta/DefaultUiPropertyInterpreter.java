package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;
import de.javaholic.toolkit.ui.annotations.UIRequired;
import de.javaholic.toolkit.ui.annotations.UiHidden;
import de.javaholic.toolkit.ui.annotations.UiLabel;
import de.javaholic.toolkit.ui.annotations.UiOrder;
import de.javaholic.toolkit.ui.annotations.UiPermission;
import de.javaholic.toolkit.ui.annotations.UiReadOnly;

import java.util.Optional;

/**
 * Default interpreter for UI annotation semantics.
 *
 * <p>Applies UI-level annotations and technical id/version detection only.</p>
 */
public class DefaultUiPropertyInterpreter implements UiPropertyInterpreter {

    /**
     * Interprets one property using default UI semantics.
     *
     * <p>Example: {@code UiProperty<User> p = new DefaultUiPropertyInterpreter().interpret(User.class, prop, meta);}</p>
     */
    @Override
    public <T> UiProperty<T> interpret(
            Class<T> beanType,
            BeanProperty<T, ?> property,
            BeanMeta<T> beanMeta
    ) {
        PropertyElements elements = PropertyElements.resolve(beanType, property.name());

        boolean technical = isTechnical(property, beanMeta);
        boolean hidden = technical || findAnnotation(UiHidden.class, elements).isPresent();
        String permissionKey = findAnnotation(UiPermission.class, elements)
                .map(UiPermission::value)
                .filter(value -> !value.isBlank())
                .orElse(null);
        boolean required = findAnnotation(UIRequired.class, elements).isPresent();
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

    // TODO: add @UiTechnical o.ä. what about @UiVisibleFor("ADMIN") or better @UiPermission("iam.user.admin")
    private static <T> boolean isTechnical(BeanProperty<T, ?> property, BeanMeta<T> beanMeta) {
        return beanMeta.idProperty().map(p -> p.name().equals(property.name())).orElse(false)
                || beanMeta.versionProperty().map(p -> p.name().equals(property.name())).orElse(false);
    }

    protected static <A extends java.lang.annotation.Annotation> Optional<A> findAnnotation(
            Class<A> annotationType,
            PropertyElements elements
    ) {
       return elements.getGetter().map( e ->e.getAnnotation(annotationType))
                        .or(() -> elements.getField().map(e-> e.getAnnotation(annotationType)))
                        .or(() -> elements.getRecordComponent().map(e-> e.getAnnotation(annotationType)))
                        .or(Optional::empty);
    }
}
