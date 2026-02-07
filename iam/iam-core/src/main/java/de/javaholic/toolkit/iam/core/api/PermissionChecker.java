package de.javaholic.toolkit.iam.core.api;
/**
 * Central authorization service.
 *
 * <p>Evaluates whether the current user has a given permission.</p>
 *
 * <p>This interface contains NO knowledge of:</p>
 * <ul>
 *   <li>Persistence</li>
 *   <li>Security frameworks</li>
 *   <li>UI or transport layers</li>
 * </ul>
 *
 * <p>Implementations typically delegate to {@link CurrentUser}
 * and inspect the user's effective permissions.</p>
 */
public interface PermissionChecker {

    boolean hasPermission(String permission);
}
