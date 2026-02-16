package de.javaholic.toolkit.iam.ui.store;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.ui.dto.UserDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.UUID;

public class UserDtoCrudStore extends DtoCrudStore<UserDto, User, UUID> {

    public UserDtoCrudStore(CrudStore<User, UUID> domainStore, DtoMapper<UserDto, User> dtoMapper) {
        super(domainStore, dtoMapper);
    }
}
