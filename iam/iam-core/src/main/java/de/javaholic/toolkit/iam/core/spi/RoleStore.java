package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Role;

import java.util.Optional;

public interface RoleStore {

    Optional<Role> findByName(String name);
}
