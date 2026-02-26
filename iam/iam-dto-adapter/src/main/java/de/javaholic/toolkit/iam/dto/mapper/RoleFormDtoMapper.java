package de.javaholic.toolkit.iam.dto.mapper;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.dto.PermissionFormDto;
import de.javaholic.toolkit.iam.dto.RoleFormDto;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.HashSet;
import java.util.Set;

public class RoleFormDtoMapper implements DtoMapper<RoleFormDto, Role> {

    private final PermissionFormDtoMapper permissionMapper;

    public RoleFormDtoMapper(PermissionFormDtoMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public RoleFormDto toDto(Role domain) {
        return new RoleFormDto(domain.getName(), toPermissionDtos(domain.getPermissions()));
    }

    @Override
    public Role toDomain(RoleFormDto dto) {
        return new Role(dto.getName(), toDomainPermissions(dto.getPermissions()));
    }

    private Set<PermissionFormDto> toPermissionDtos(Set<Permission> permissions) {
        Set<Permission> source = permissions != null ? permissions : Set.of();
        Set<PermissionFormDto> result = new HashSet<>(source.size());
        for (Permission permission : source) {
            result.add(permissionMapper.toDto(permission));
        }
        return result;
    }

    private Set<Permission> toDomainPermissions(Set<PermissionFormDto> permissions) {
        Set<PermissionFormDto> source = permissions != null ? permissions : Set.of();
        Set<Permission> result = new HashSet<>(source.size());
        for (PermissionFormDto permission : source) {
            result.add(permissionMapper.toDomain(permission));
        }
        return result;
    }
}

