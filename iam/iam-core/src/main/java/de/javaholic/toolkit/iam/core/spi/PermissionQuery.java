package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-side SPI for querying {@link Permission} entities.
 */
public interface PermissionQuery {

    /**
     * Finds a permission by unique code.
     */
    Optional<Permission> findByCode(String code);

    /**
     * Returns all permissions.
     */
    List<Permission> findAll();

    /**
     * Finds a permission by id.
     */
    Optional<Permission> findById(UUID id);
}
