package de.javaholic.toolkit.iam.core.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Role {

    private final String name;
    private final Set<Permission> permissions;

    public Role(String name, Set<Permission> permissions) {
        this.name = Objects.requireNonNull(name, "name");
        this.permissions = Set.copyOf(Objects.requireNonNull(permissions, "permissions"));
    }

    public String getName() {
        return name;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    /**
     * Equality is based on role name, which is the stable identifier.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role role)) {
            return false;
        }
        return name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
