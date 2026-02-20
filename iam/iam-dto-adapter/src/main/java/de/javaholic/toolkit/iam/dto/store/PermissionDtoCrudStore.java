package de.javaholic.toolkit.iam.dto.store;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.dto.PermissionDto;
import de.javaholic.toolkit.iam.dto.spi.PermissionDtoStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.UUID;

public class PermissionDtoCrudStore extends DtoCrudStore<PermissionDto, Permission, UUID> implements PermissionDtoStore {

    public PermissionDtoCrudStore(CrudStore<Permission, UUID> domainStore, DtoMapper<PermissionDto, Permission> dtoMapper) {
        super(domainStore, dtoMapper);
    }
}
