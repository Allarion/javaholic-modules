package de.javaholic.toolkit.iam.core.api;
/**
 * Domain SPI for authorization decisions in IAM.
 *
 * <p>This contract keeps authorization checks framework-agnostic and aligned with explicit
 * dependency direction (core declares; adapters provide implementations).</p>
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Evaluate whether the active principal has a named permission.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>IAM/security adapters that combine {@link CurrentUser} with permission data sources.</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>Application services, UI action guards, and method-level authorization components.</li>
 * </ul>
 *
 * <p><strong>Mode Interaction</strong></p>
 * <ul>
 *   <li>Rapid and Clean modes: identical API and semantics; persistence mode does not change authorization SPI.</li>
 * </ul>
 */
public interface PermissionChecker {

    boolean hasPermission(String permission);
}
