package de.javaholic.toolkit.iam.dto.spi;

import de.javaholic.toolkit.iam.dto.UserFormDto;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

public interface UserFormDtoStore extends CrudStore<UserFormDto, UUID> {
}
