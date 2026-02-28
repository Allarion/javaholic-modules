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
    /**
     * Finds a user by unique identifier.
     */
    Optional<User> findByIdentifier(String identifier);

    /**
     * Returns all users.
     */
    List<User> findAll();

    /**
     * Finds a user by id.
     */
    Optional<User> findById(UUID id);
}
