package de.javaholic.toolkit.iam.dto.spi;

import de.javaholic.toolkit.iam.dto.PermissionFormDto;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

public interface PermissionFormDtoStore extends CrudStore<PermissionFormDto, UUID> {
}
