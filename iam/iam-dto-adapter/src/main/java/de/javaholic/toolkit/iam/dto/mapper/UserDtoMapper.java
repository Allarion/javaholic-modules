package de.javaholic.toolkit.iam.dto.mapper;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.dto.dto.UserDto;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.Set;

public class UserDtoMapper implements DtoMapper<UserDto, User> {

    @Override
    public UserDto toDto(User domain) {
        return new UserDto(
            domain.getId(),
            domain.getUsername(),
            domain.getStatus(),
            domain.getRoles() != null ? Set.copyOf(domain.getRoles()) : Set.of()
        );
    }

    @Override
    public User toDomain(UserDto dto) {
        return new User(
            dto.getId(),
            dto.getUsername(),
            dto.getStatus(),
            dto.getRoles() != null ? Set.copyOf(dto.getRoles()) : Set.of()
        );
    }
}

