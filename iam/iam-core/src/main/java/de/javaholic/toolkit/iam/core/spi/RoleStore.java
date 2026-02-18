package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

/**
 * Combined read/write SPI for roles.
 */
public interface RoleStore extends RoleQuery, RoleCommand, CrudStore<Role, UUID> {
}
