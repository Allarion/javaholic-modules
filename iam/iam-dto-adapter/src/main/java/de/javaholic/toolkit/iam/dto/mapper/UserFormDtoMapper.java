package de.javaholic.toolkit.iam.dto.mapper;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.dto.RoleFormDto;
import de.javaholic.toolkit.iam.dto.UserFormDto;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.HashSet;
import java.util.Set;

public class UserFormDtoMapper implements DtoMapper<UserFormDto, User> {

    private final RoleFormDtoMapper roleMapper;

    public UserFormDtoMapper(RoleFormDtoMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public UserFormDto toDto(User domain) {
        return new UserFormDto(
            domain.getId(),
            domain.getUsername(),
            domain.getStatus(),
            toRoleDtos(domain.getRoles())
        );
    }

    @Override
    public User toDomain(UserFormDto dto) {
        return new User(
            dto.getId(),
            dto.getUsername(),
            dto.getStatus(),
            toDomainRoles(dto.getRoles())
        );
    }

    private Set<RoleFormDto> toRoleDtos(Set<Role> roles) {
        Set<Role> source = roles != null ? roles : Set.of();
        Set<RoleFormDto> result = new HashSet<>(source.size());
        for (Role role : source) {
            result.add(roleMapper.toDto(role));
        }
        return result;
    }

    private Set<Role> toDomainRoles(Set<RoleFormDto> roles) {
        Set<RoleFormDto> source = roles != null ? roles : Set.of();
        Set<Role> result = new HashSet<>(source.size());
        for (RoleFormDto role : source) {
            result.add(roleMapper.toDomain(role));
        }
        return result;
    }
}
