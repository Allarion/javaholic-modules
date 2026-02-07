package de.javaholic.toolkit.iam.core.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class User {

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
