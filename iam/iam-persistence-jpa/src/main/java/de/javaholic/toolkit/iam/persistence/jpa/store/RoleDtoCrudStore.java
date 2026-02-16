package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.dto.RoleDto;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.RoleDtoMapper;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;

import java.util.UUID;

public class RoleDtoCrudStore extends DtoCrudStore<RoleDto, Role, UUID> {

    public RoleDtoCrudStore(CrudStore<Role, UUID> domainStore, RoleDtoMapper mapper) {
        super(domainStore, mapper);
    }
}
