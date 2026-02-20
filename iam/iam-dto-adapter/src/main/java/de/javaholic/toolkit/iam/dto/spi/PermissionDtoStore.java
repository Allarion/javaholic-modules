package de.javaholic.toolkit.iam.dto.spi;

import de.javaholic.toolkit.iam.dto.PermissionDto;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

public interface PermissionDtoStore extends CrudStore<PermissionDto, UUID> {
}
