package de.javaholic.toolkit.iam.core.domain;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
/**
 * Domain entity representing a persisted user.
 *
 * <p>This is the authoritative representation of a user in the system.</p>
 *
 * <ul>
 *   <li>Represents long-lived state (identity, status, role assignments)</li>
 *   <li>Is persisted via a {@code UserStore} (file, JPA, etc.)</li>
 *   <li>Independent of security context, requests, or sessions</li>
 * </ul>
 *
 * <p>IMPORTANT:</p>
 * <ul>
 *   <li>Do NOT use this class directly for authorization checks</li>
 *   <li>Do NOT expose this as a security principal</li>
 * </ul>
 *
 * <p>{@link de.javaholic.toolkit.iam.core.api.UserPrincipal}
 * is a derived, runtime-only projection of this entity.</p>
 */
public final class User {
// TODO: Wrap the ID in UserId(UUID)?
    private final UUID id;
    private final String username;
    private final UserStatus status;
    private final Set<Role> roles;

    public User(UUID id, String username, UserStatus status, Set<Role> roles) {
        this.id = Objects.requireNonNull(id, "id");
        this.username = Objects.requireNonNull(username, "username");
        this.status = Objects.requireNonNull(status, "status");
        this.roles = Set.copyOf(Objects.requireNonNull(roles, "roles"));
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Equality is based on user id, which is the stable identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
