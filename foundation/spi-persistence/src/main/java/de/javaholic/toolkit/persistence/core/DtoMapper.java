package de.javaholic.toolkit.persistence.core;

/**
 * Mapper contract between DTO and domain model.
 *
 * <p>Example:</p>
 * <pre>{@code
 * final class UserDtoMapper implements DtoMapper<UserDto, User> {
 *     public UserDto toDto(User domain) { ... }
 *     public User toDomain(UserDto dto) { ... }
 * }
 * }</pre>
 */
public interface DtoMapper<DTO, D> {

    /**
     * Maps one domain instance to DTO.
     */
    DTO toDto(D domain);

    /**
     * Maps one DTO instance to domain.
     */
    D toDomain(DTO dto);
}
