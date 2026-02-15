package de.javaholic.toolkit.iam.persistence.jpa.mapper;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaPermissionEntity;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaRoleEntity;
import de.javaholic.toolkit.persistence.core.EntityMapper;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class JpaRoleMapper implements EntityMapper<Role, JpaRoleEntity> {

    private final JpaPermissionMapper permissionMapper;

    public JpaRoleMapper(JpaPermissionMapper permissionMapper) {
        this.permissionMapper = Objects.requireNonNull(permissionMapper, "permissionMapper");
    }

    @Override
    public Role toDomain(JpaRoleEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new Role(entity.getName(), toDomainPermissions(entity.getPermissions()));
    }

    @Override
    public JpaRoleEntity toEntity(Role role) {
        Objects.requireNonNull(role, "role");
        JpaRoleEntity entity = new JpaRoleEntity();
        entity.setId(deterministicId("role:", role.getName()));
        entity.setName(role.getName());
        entity.setPermissions(toJpaPermissions(role.getPermissions()));
        return entity;
    }

    private Set<Permission> toDomainPermissions(Set<JpaPermissionEntity> entities) {
        Set<JpaPermissionEntity> source = entities != null ? entities : Set.of();
        Set<Permission> result = new HashSet<>(source.size());
        for (JpaPermissionEntity entity : source) {
            result.add(permissionMapper.toDomain(entity));
        }
        return result;
    }

    private Set<JpaPermissionEntity> toJpaPermissions(Set<Permission> permissions) {
        Set<Permission> source = permissions != null ? permissions : Set.of();
        Set<JpaPermissionEntity> result = new HashSet<>(source.size());
        for (Permission permission : source) {
            result.add(permissionMapper.toEntity(permission));
        }
        return result;
    }

    private UUID deterministicId(String prefix, String value) {
        return UUID.nameUUIDFromBytes((prefix + value).getBytes(StandardCharsets.UTF_8));
    }
}
