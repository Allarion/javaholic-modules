package de.javaholic.toolkit.ui.meta;

import jakarta.persistence.Entity;

/**
 * Factory for selecting the UI property interpretation strategy.
 */
public final class UiPropertyInterpreterFactory {

    private UiPropertyInterpreterFactory() {
    }

    /**
     * Creates an interpreter for the given bean type.
     *
     * <p>Example: {@code UiPropertyInterpreter interpreter = UiPropertyInterpreterFactory.create(UserEntity.class);}</p>
     */
    public static UiPropertyInterpreter create(Class<?> beanType) {
        if (beanType.isAnnotationPresent(Entity.class)) {
            return new JpaUiPropertyInterpreter();
        }
        return new DefaultUiPropertyInterpreter();
    }
}
