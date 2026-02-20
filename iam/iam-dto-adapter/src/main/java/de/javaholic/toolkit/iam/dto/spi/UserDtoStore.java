package de.javaholic.toolkit.iam.dto.spi;

import de.javaholic.toolkit.iam.dto.UserDto;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

public interface UserDtoStore extends CrudStore<UserDto, UUID> {
}
