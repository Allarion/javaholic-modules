package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.User;

public interface UserCommand {
    User save(User user);
    void delete(User user);
}