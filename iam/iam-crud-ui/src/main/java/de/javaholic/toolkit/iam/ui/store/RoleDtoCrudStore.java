package de.javaholic.toolkit.iam.ui.store;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.ui.dto.RoleDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.UUID;

public class RoleDtoCrudStore extends DtoCrudStore<RoleDto, Role, UUID> {

    public RoleDtoCrudStore(CrudStore<Role, UUID> domainStore, DtoMapper<RoleDto, Role> dtoMapper) {
        super(domainStore, dtoMapper);
    }
}
