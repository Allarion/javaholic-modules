package de.javaholic.toolkit.iam.dto.mapper;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.dto.PermissionDto;
import de.javaholic.toolkit.persistence.core.DtoMapper;

public class PermissionDtoMapper implements DtoMapper<PermissionDto, Permission> {

    @Override
    public PermissionDto toDto(Permission domain) {
        return new PermissionDto(domain.getCode());
    }

    @Override
    public Permission toDomain(PermissionDto dto) {
        return new Permission(dto.getCode());
    }
}

