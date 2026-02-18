package de.javaholic.toolkit.ui.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a UI property with a permission key.
 *
 * <p>Example:</p>
 * <pre>{@code
 * @UiPermission("iam.user.edit")
 * private String email;
 * }</pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
public @interface UiPermission {

    /**
     * Permission key used by UI-level access checks.
     *
     * <p>Example: {@code "iam.user.edit"}</p>
     */
    String value();
}
