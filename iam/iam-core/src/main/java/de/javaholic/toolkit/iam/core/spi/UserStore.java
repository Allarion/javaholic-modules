package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

/**
 * Combined read/write SPI for users.
 */
public interface UserStore extends UserQuery, UserCommand, CrudStore<User, UUID> {
}
