package de.javaholic.toolkit.iam.dto.mapper;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.dto.PermissionFormDto;
import de.javaholic.toolkit.persistence.core.DtoMapper;

public class PermissionFormDtoMapper implements DtoMapper<PermissionFormDto, Permission> {

    @Override
    public PermissionFormDto toDto(Permission domain) {
        return new PermissionFormDto(domain.getCode());
    }

    @Override
    public Permission toDomain(PermissionFormDto dto) {
        return new Permission(dto.getCode());
    }
}

