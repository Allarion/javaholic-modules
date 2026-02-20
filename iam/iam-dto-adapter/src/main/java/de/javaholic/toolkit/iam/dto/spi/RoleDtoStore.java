package de.javaholic.toolkit.iam.dto.spi;

import de.javaholic.toolkit.iam.dto.RoleDto;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

public interface RoleDtoStore extends CrudStore<RoleDto, UUID> {
}
