package de.javaholic.toolkit.iam.core.domain;

/**
 * Lifecycle status of a user account.
 *
 * <p>Example: {@code if (user.status() == UserStatus.ACTIVE) { ... }}</p>
 */
public enum UserStatus {
    /** User is active and allowed to sign in. */
    ACTIVE,
    /** User is intentionally deactivated. */
    DISABLED,
    /** User is locked due to security or policy reasons. */
    LOCKED
}
