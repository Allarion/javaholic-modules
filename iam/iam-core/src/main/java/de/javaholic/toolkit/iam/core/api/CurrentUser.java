package de.javaholic.toolkit.iam.core.api;

import java.util.Optional;
/**
 * Domain SPI access point for the current authenticated user.
 *
 * <p>This is IAM bounded-context SPI, not platform infrastructure SPI. It keeps authorization
 * logic decoupled from specific security frameworks and runtime environments.</p>
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Expose the current {@link UserPrincipal} in the active execution context.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>Security adapters (for example Spring Security bridge adapters).</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>IAM services such as permission evaluation and audit decisions.</li>
 * </ul>
 *
 */
public interface CurrentUser {

    Optional<UserPrincipal> get();
}
