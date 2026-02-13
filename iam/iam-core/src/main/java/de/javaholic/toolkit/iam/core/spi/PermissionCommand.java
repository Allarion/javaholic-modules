package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Permission;

public interface PermissionCommand {
    Permission save(Permission user);
    void delete(Permission user);
}