package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;
import jakarta.persistence.Column;

/**
 * JPA-aware interpreter that extends default UI semantics with persistence-required rules.
 */
public final class JpaUiPropertyInterpreter extends DefaultUiPropertyInterpreter {

    /**
     * Interprets one property and applies {@code @Column(nullable=false)} as additional required semantics.
     *
     * <p>Example: {@code UiProperty<UserEntity> p = new JpaUiPropertyInterpreter().interpret(UserEntity.class, prop, meta);}</p>
     */
    @Override
    public <T> UiProperty<T> interpret(
            Class<T> beanType,
            BeanProperty<T, ?> property,
            BeanMeta<T> beanMeta
    ) {
        UiProperty<T> uiProperty = super.interpret(beanType, property, beanMeta);
        PropertyElements elements = PropertyElements.resolve(beanType, property.name());
        boolean requiredByColumn = findAnnotation(Column.class, elements)
                .map(column -> !column.nullable())
                .orElse(false);

        if (!requiredByColumn) {
            return uiProperty;
        }

        return new UiProperty<>(
                beanMeta,
                property,
                uiProperty.isHidden(),
                uiProperty.isTechnical(),
                true,
                uiProperty.permissionKey().orElse(null),
                uiProperty.labelKey(),
                uiProperty.order(),
                uiProperty.isReadOnly()
        );
    }
}
