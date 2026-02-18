package de.javaholic.toolkit.persistence.core;

/**
 * Platform SPI mapper between DTO and domain model.
 *
 * <p>This mapper belongs to the adapter layer. It keeps DTO concerns out of domain and persistence
 * implementations while preserving explicit, non-reflective mapping.</p>
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Translate DTO objects to domain objects and back.</li>
 *   <li>Define clean adapter boundaries for Clean Mode.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>DTO adapter modules for each bounded context.</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>{@code DtoCrudStore} and DTO adapter auto-configuration.</li>
 * </ul>
 *
 * <p><strong>Mode Interaction</strong></p>
 * <ul>
 *   <li>Rapid Mode: not used because DTO adapter is bypassed.</li>
 *   <li>Clean Mode: required to map between DTO-facing stores and domain stores.</li>
 * </ul>
 *
 * <p>Example usage:</p>
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
