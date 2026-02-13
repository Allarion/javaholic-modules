package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Role;

public interface RoleCommand {
    Role save(Role user);
    void delete(Role user);
}