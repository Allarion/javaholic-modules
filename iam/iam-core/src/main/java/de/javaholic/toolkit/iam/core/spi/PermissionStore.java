package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

/**
 * Combined read/write SPI for permissions.
 *
 * <p>Implementations usually delegate to a persistence adapter (JPA, JDBC, etc.).</p>
 */
public interface PermissionStore extends PermissionCommand, PermissionQuery, CrudStore<Permission, UUID> {
}
