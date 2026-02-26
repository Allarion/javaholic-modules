package de.javaholic.toolkit.iam.dto.store;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.dto.RoleFormDto;
import de.javaholic.toolkit.iam.dto.spi.RoleDtoStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.UUID;

public class RoleFormDtoCrudStore extends DtoCrudStore<RoleFormDto, Role, UUID> implements RoleDtoStore {

    public RoleFormDtoCrudStore(CrudStore<Role, UUID> domainStore, DtoMapper<RoleFormDto, Role> dtoMapper) {
        super(domainStore, dtoMapper);
    }
}
