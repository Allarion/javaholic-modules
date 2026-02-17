package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Role;

/**
 * Write-side SPI for managing {@link Role} entities.
 */
public interface RoleCommand {

    /**
     * Creates or updates a role.
     */
    Role save(Role user);

    /**
     * Deletes a role.
     */
    void delete(Role user);
}
