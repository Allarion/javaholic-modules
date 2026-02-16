package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.dto.UserDto;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.UserDtoMapper;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;

import java.util.UUID;

public class UserDtoCrudStore extends DtoCrudStore<UserDto, User, UUID> {

    public UserDtoCrudStore(CrudStore<User, UUID> domainStore, UserDtoMapper mapper) {
        super(domainStore, mapper);
    }
}
