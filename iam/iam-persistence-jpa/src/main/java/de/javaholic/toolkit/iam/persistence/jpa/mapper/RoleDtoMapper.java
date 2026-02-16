package de.javaholic.toolkit.iam.persistence.jpa.mapper;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.dto.PermissionDto;
import de.javaholic.toolkit.iam.core.dto.RoleDto;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.HashSet;
import java.util.Set;

public class RoleDtoMapper implements DtoMapper<RoleDto, Role> {

    private final PermissionDtoMapper permissionMapper;

    public RoleDtoMapper(PermissionDtoMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public RoleDto toDto(Role domain) {
        return new RoleDto(domain.getName(), toPermissionDtos(domain.getPermissions()));
    }

    @Override
    public Role toDomain(RoleDto dto) {
        return new Role(dto.getName(), toDomainPermissions(dto.getPermissions()));
    }

    private Set<PermissionDto> toPermissionDtos(Set<Permission> permissions) {
        Set<Permission> source = permissions != null ? permissions : Set.of();
        Set<PermissionDto> result = new HashSet<>(source.size());
        for (Permission permission : source) {
            result.add(permissionMapper.toDto(permission));
        }
        return result;
    }

    private Set<Permission> toDomainPermissions(Set<PermissionDto> permissions) {
        Set<PermissionDto> source = permissions != null ? permissions : Set.of();
        Set<Permission> result = new HashSet<>(source.size());
        for (PermissionDto permission : source) {
            result.add(permissionMapper.toDomain(permission));
        }
        return result;
    }
}
