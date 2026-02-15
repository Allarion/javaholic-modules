package de.javaholic.toolkit.ui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the i18n label key for this property in auto UI.
 *
 * <p>The key is stored in UiMeta as semantic metadata. It is not resolved here.
 * Display text is resolved later by a TextResolver in the UI builders.</p>
 *
 * <p>Affects: {@code Grids.auto(...)} and {@code Forms.auto(...)}</p>
 *
 * <p>Evaluated by: UiMeta only</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * public final class UserDto {
 *     @UiLabel(key = "user.email.label")
 *     private String email;
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UiLabel {

    /**
     * The i18n key used to resolve display text.
     */
    String key();
}
