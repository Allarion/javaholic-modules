package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.User;

/**
 * Write-side SPI for managing {@link User} entities.
 */
public interface UserCommand {

    /**
     * Creates or updates a user.
     */
    User save(User user);

    /**
     * Deletes a user.
     */
    void delete(User user);
}
