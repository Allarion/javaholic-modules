package de.javaholic.toolkit.iam.dto.store;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.dto.RoleDto;
import de.javaholic.toolkit.iam.dto.spi.RoleDtoStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.UUID;

public class RoleDtoCrudStore extends DtoCrudStore<RoleDto, Role, UUID> implements RoleDtoStore {

    public RoleDtoCrudStore(CrudStore<Role, UUID> domainStore, DtoMapper<RoleDto, Role> dtoMapper) {
        super(domainStore, dtoMapper);
    }
}
