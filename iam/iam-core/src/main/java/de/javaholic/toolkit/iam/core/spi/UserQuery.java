package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.api.UserPrincipal;
import de.javaholic.toolkit.iam.core.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence abstraction for users.
 *
 * <p>This SPI allows IAM core to load users from different sources
 * (files, databases, remote services) without coupling to a specific
 * storage technology.</p>
 *
 * <p>Implementations must return domain {@link User} objects,
 * never {@link UserPrincipal}.</p>
 */

public interface UserQuery {
    Optional<User> findByUsername(String username);
    List<User> findAll();
    Optional<User> findById(UUID id);
}

