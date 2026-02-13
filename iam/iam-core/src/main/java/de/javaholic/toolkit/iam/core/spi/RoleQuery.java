package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleQuery {

    Optional<Role> findByName(String name);

    List<Role> findAll();

    Optional<Role> findById(UUID id);
}
