package de.javaholic.toolkit.iam.persistence.jpa.mapper;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaRoleEntity;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaUserEntity;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class JpaUserMapper {

    private final JpaRoleMapper roleMapper;

    public JpaUserMapper(JpaRoleMapper roleMapper) {
        this.roleMapper = Objects.requireNonNull(roleMapper, "roleMapper");
    }

    public User toDomain(JpaUserEntity entity) {
        Objects.requireNonNull(entity, "entity");
        return new User(
            entity.getId(),
            entity.getUsername(),
            entity.getStatus(),
            toDomainRoles(entity.getRoles())
        );
    }

    public JpaUserEntity toJpa(User user) {
        Objects.requireNonNull(user, "user");
        JpaUserEntity entity = new JpaUserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setStatus(user.getStatus());
        entity.setRoles(toJpaRoles(user.getRoles()));
        return entity;
    }

    private Set<Role> toDomainRoles(Set<JpaRoleEntity> entities) {
        Set<JpaRoleEntity> source = entities != null ? entities : Set.of();
        Set<Role> result = new HashSet<>(source.size());
        for (JpaRoleEntity entity : source) {
            result.add(roleMapper.toDomain(entity));
        }
        return result;
    }

    private Set<JpaRoleEntity> toJpaRoles(Set<Role> roles) {
        Set<Role> source = roles != null ? roles : Set.of();
        Set<JpaRoleEntity> result = new HashSet<>(source.size());
        for (Role role : source) {
            result.add(roleMapper.toJpa(role));
        }
        return result;
    }
}
