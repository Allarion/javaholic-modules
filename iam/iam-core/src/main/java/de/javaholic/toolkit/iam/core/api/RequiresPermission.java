package de.javaholic.toolkit.iam.core.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Marks a method or type as requiring a specific permission.
 * (Backend Enforcement)
 * <p>This annotation is a declarative marker only.</p>
 *
 * <p>Enforcement is handled by infrastructure code
 * (e.g. AOP, Spring Security AuthorizationManager)
 * and is intentionally not part of IAM core.</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {

    String value();
}
