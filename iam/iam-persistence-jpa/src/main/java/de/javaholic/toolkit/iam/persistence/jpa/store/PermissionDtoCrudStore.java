package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.dto.PermissionDto;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.PermissionDtoMapper;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;

import java.util.UUID;

public class PermissionDtoCrudStore extends DtoCrudStore<PermissionDto, Permission, UUID> {

    public PermissionDtoCrudStore(CrudStore<Permission, UUID> domainStore, PermissionDtoMapper mapper) {
        super(domainStore, mapper);
    }
}
