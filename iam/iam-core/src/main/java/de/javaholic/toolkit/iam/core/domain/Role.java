package de.javaholic.toolkit.iam.core.domain;

import java.util.Objects;
import java.util.Set;
/**
 * Domain entity representing a role.
 *
 * <p>A role is a named grouping of permissions assigned to users.</p>
 *
 * <ul>
 *   <li>Roles are persisted domain concepts</li>
 *   <li>They may change over time (permissions added/removed)</li>
 * </ul>
 *
 * <p>Roles are resolved into permission codes when creating a
 * {@link de.javaholic.toolkit.iam.core.api.UserPrincipal}.</p>
 */
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
