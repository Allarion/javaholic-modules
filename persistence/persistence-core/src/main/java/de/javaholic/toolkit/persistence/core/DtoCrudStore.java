package de.javaholic.toolkit.persistence.core;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link CrudStore} adapter that exposes DTOs while delegating persistence to a domain store.
 *
 * <p>Concept: translate at the boundary only. The wrapped store remains domain-centric and this
 * adapter performs DTO <-> domain mapping on every operation.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * CrudStore<User, UUID> domainStore = ...;
 * DtoMapper<UserDto, User> mapper = ...;
 * CrudStore<UserDto, UUID> dtoStore = new DtoCrudStore<>(domainStore, mapper);
 * }</pre>
 */
public class DtoCrudStore<DTO, D, ID> implements CrudStore<DTO, ID> {

    private final CrudStore<D, ID> domainStore;
    private final DtoMapper<DTO, D> mapper;

    /**
     * Creates the adapter with target domain store and mapper.
     */
    public DtoCrudStore(CrudStore<D, ID> domainStore, DtoMapper<DTO, D> mapper) {
        this.domainStore = Objects.requireNonNull(domainStore);
        this.mapper = Objects.requireNonNull(mapper);
    }

    /**
     * Loads all domain entities and maps them to DTOs.
     */
    @Override
    public List<DTO> findAll() {
        return domainStore.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Loads one domain entity by id and maps it to DTO.
     */
    @Override
    public Optional<DTO> findById(ID id) {
        return domainStore.findById(id)
                .map(mapper::toDto);
    }

    /**
     * Maps DTO to domain, persists it, and maps persisted state back to DTO.
     */
    @Override
    public DTO save(DTO dto) {
        D saved = domainStore.save(mapper.toDomain(dto));
        return mapper.toDto(saved);
    }

    /**
     * Maps DTO to domain and delegates deletion to the wrapped store.
     */
    @Override
    public void delete(DTO dto) {
        domainStore.delete(mapper.toDomain(dto));
    }
}
