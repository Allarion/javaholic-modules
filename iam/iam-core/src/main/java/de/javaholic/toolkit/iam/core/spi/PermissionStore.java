package de.javaholic.toolkit.iam.core.spi;

/**
 * Combined read/write SPI for permissions.
 *
 * <p>Implementations usually delegate to a persistence adapter (JPA, JDBC, etc.).</p>
 */
public interface PermissionStore extends PermissionCommand, PermissionQuery{
}
