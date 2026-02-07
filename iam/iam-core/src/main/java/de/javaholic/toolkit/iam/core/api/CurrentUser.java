package de.javaholic.toolkit.iam.core.api;

import java.util.Optional;
/**
 * Access point for the current authenticated user.
 *
 * <p>Provides a {@link UserPrincipal} for the current execution context,
 * if available.</p>
 *
 * <p>This abstraction decouples IAM core logic from the underlying
 * authentication mechanism (Spring Security, CLI, tests, etc.).</p>
 */
public interface CurrentUser {

    Optional<UserPrincipal> get();
}
