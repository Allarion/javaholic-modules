package de.javaholic.toolkit.iam.persistence.jpa.mapper;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaPermissionEntity;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public final class JpaPermissionMapper {

    public Permission toDomain(JpaPermissionEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new Permission(entity.getCode());
    }

    public JpaPermissionEntity toJpa(Permission permission) {
        Objects.requireNonNull(permission, "permission");
        JpaPermissionEntity entity = new JpaPermissionEntity();
        entity.setId(deterministicId("perm:", permission.getCode()));
        entity.setCode(permission.getCode());
        return entity;
    }

    private UUID deterministicId(String prefix, String value) {
        return UUID.nameUUIDFromBytes((prefix + value).getBytes(StandardCharsets.UTF_8));
    }
}
