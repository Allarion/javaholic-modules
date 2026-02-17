package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Permission;

/**
 * Write-side SPI for managing {@link Permission} entities.
 *
 * <p>Concept: command interfaces isolate mutating use cases from query contracts.</p>
 */
public interface PermissionCommand {

    /**
     * Creates or updates a permission.
     */
    Permission save(Permission user);

    /**
     * Deletes a permission.
     */
    void delete(Permission user);
}
