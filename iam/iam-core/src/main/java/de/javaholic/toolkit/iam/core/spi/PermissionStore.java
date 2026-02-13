package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionStore {

    Optional<Permission> findByCode(String code);

    default List<Permission> findAll() {
        return List.of();
    }
}
