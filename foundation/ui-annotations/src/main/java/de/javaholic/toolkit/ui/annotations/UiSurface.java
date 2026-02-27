package de.javaholic.toolkit.ui.annotations;

import java.lang.annotation.*;

/**
 * Declarative surface metadata for a DTO type.
 *
 * <p>This annotation is declarative only and must not contain runtime UI logic.</p>
 * <p>If {@link #actions()} is {@link Void}, no action provider is configured.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UiSurface {

    Class<?> view(); // default ResourcePanel.class;

    Class<?> actions() default Void.class;
}
