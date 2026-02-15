package de.javaholic.toolkit.persistence.core;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DtoCrudStore<DTO, D, ID> implements CrudStore<DTO, ID> {

    private final CrudStore<D, ID> domainStore;
    private final DtoMapper<DTO, D> mapper;

    public DtoCrudStore(CrudStore<D, ID> domainStore, DtoMapper<DTO, D> mapper) {
        this.domainStore = Objects.requireNonNull(domainStore);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public List<DTO> findAll() {
        return domainStore.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Optional<DTO> findById(ID id) {
        return domainStore.findById(id)
                .map(mapper::toDto);
    }

    @Override
    public DTO save(DTO dto) {
        D saved = domainStore.save(mapper.toDomain(dto));
        return mapper.toDto(saved);
    }

    @Override
    public void delete(DTO dto) {
        domainStore.delete(mapper.toDomain(dto));
    }
}
