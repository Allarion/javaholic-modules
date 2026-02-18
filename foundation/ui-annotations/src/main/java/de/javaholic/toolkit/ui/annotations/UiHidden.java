package de.javaholic.toolkit.ui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that this property should not be shown in auto UI.
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
 *     @UiHidden
 *     private java.util.UUID id;
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UiHidden {
}
