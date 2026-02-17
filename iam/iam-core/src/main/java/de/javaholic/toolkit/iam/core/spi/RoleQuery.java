package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-side SPI for querying {@link Role} entities.
 */
public interface RoleQuery {

    /**
     * Finds a role by unique name.
     */
    Optional<Role> findByName(String name);

    /**
     * Returns all roles.
     */
    List<Role> findAll();

    /**
     * Finds a role by id.
     */
    Optional<Role> findById(UUID id);
}
