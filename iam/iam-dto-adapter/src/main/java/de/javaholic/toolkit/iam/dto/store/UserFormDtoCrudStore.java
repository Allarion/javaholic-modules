package de.javaholic.toolkit.iam.dto.store;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.dto.UserFormDto;
import de.javaholic.toolkit.iam.dto.spi.UserFormDtoStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.UUID;

public class UserFormDtoCrudStore extends DtoCrudStore<UserFormDto, User, UUID> implements UserFormDtoStore {

    public UserFormDtoCrudStore(CrudStore<User, UUID> domainStore, DtoMapper<UserFormDto, User> dtoMapper) {
        super(domainStore, dtoMapper);
    }
}
