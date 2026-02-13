package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionQuery {

    Optional<Permission> findByCode(String code);

    List<Permission> findAll();

    Optional<Permission> findById(UUID id);
}
