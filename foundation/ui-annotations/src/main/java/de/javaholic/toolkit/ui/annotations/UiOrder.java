package de.javaholic.toolkit.ui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the UI ordering value for this property in auto UI.
 *
 * <p>Lower values appear first. Properties without explicit order use default
 * fallback ordering.</p>
 *
 * <p>Affects: {@code Grids.auto(...)} and {@code Forms.auto(...)}</p>
 *
 * <p>Evaluated by: UiMeta only</p>
 *
 * <p>Label resolution is external via TextResolver.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * public final class UserDto {
 *     @UiOrder(10)
 *     private String email;
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UiOrder {

    /**
     * Sort value used for auto-generated UI ordering.
     */
    int value();
}
