package de.javaholic.toolkit.iam.core.api;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
/**
 * Runtime security projection of a {@link de.javaholic.toolkit.iam.core.domain.User}.
 *
 * <p>This object represents the authenticated user in the current execution
 * context (request, thread, session).</p>
 *
 * <ul>
 *   <li>Derived from domain entities (User, Role, Permission)</li>
 *   <li>Flattened and immutable</li>
 *   <li>NOT persisted</li>
 *   <li>Valid only for the current context</li>
 * </ul>
 *
 * <p>Use this class for:</p>
 * <ul>
 *   <li>Authorization checks</li>
 *   <li>Security-related decisions</li>
 * </ul>
 *
 * <p>Do NOT:</p>
 * <ul>
 *   <li>Store this object</li>
 *   <li>Modify it</li>
 *   <li>Use it as a replacement for {@code domain.User}</li>
 * </ul>
 */
public final class UserPrincipal {

    private final UUID userId;
    private final String username;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final boolean active;

    public UserPrincipal(UUID userId, String username, Set<String> roles, Set<String> permissions, boolean active) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.username = Objects.requireNonNull(username, "username");
        this.roles = Set.copyOf(Objects.requireNonNull(roles, "roles"));
        this.permissions = Set.copyOf(Objects.requireNonNull(permissions, "permissions"));
        this.active = active;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public boolean isActive() {
        return active;
    }
}
