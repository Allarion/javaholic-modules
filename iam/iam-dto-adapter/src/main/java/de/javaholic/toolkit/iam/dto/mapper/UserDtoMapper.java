package de.javaholic.toolkit.iam.dto.mapper;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.dto.RoleDto;
import de.javaholic.toolkit.iam.dto.UserDto;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.HashSet;
import java.util.Set;

public class UserDtoMapper implements DtoMapper<UserDto, User> {

    private final RoleDtoMapper roleMapper;

    public UserDtoMapper(RoleDtoMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Override
    public UserDto toDto(User domain) {
        return new UserDto(
            domain.getId(),
            domain.getUsername(),
            domain.getStatus(),
            toRoleDtos(domain.getRoles())
        );
    }

    @Override
    public User toDomain(UserDto dto) {
        return new User(
            dto.getId(),
            dto.getUsername(),
            dto.getStatus(),
            toDomainRoles(dto.getRoles())
        );
    }

    private Set<RoleDto> toRoleDtos(Set<Role> roles) {
        Set<Role> source = roles != null ? roles : Set.of();
        Set<RoleDto> result = new HashSet<>(source.size());
        for (Role role : source) {
            result.add(roleMapper.toDto(role));
        }
        return result;
    }

    private Set<Role> toDomainRoles(Set<RoleDto> roles) {
        Set<RoleDto> source = roles != null ? roles : Set.of();
        Set<Role> result = new HashSet<>(source.size());
        for (RoleDto role : source) {
            result.add(roleMapper.toDomain(role));
        }
        return result;
    }
}
