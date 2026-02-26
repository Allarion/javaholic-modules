package de.javaholic.toolkit.iam.dto.store;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.dto.PermissionFormDto;
import de.javaholic.toolkit.iam.dto.spi.PermissionFormDtoStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.UUID;

public class PermissionFormDtoCrudStore extends DtoCrudStore<PermissionFormDto, Permission, UUID> implements PermissionFormDtoStore {

    public PermissionFormDtoCrudStore(CrudStore<Permission, UUID> domainStore, DtoMapper<PermissionFormDto, Permission> dtoMapper) {
        super(domainStore, dtoMapper);
    }
}
