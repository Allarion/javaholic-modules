package de.javaholic.toolkit.persistence.core;

public interface DtoMapper<DTO, D> {

    DTO toDto(D domain);

    D toDomain(DTO dto);
}
