package de.javaholic.toolkit.i18n.ui.mapper;

import de.javaholic.toolkit.i18n.persistence.jpa.domain.I18nEntry;
import de.javaholic.toolkit.i18n.ui.dto.I18nEntryDto;
import de.javaholic.toolkit.persistence.core.DtoMapper;

public class I18nEntryDtoMapper implements DtoMapper<I18nEntryDto, I18nEntry> {

    @Override
    public I18nEntryDto toDto(I18nEntry domain) {
        return new I18nEntryDto(
                domain.getKey(),
                domain.getLocale(),
                domain.getValue()
        );
    }

    @Override
    public I18nEntry toDomain(I18nEntryDto dto) {
        I18nEntry domain = new I18nEntry();
        domain.setKey(dto.getKey());
        domain.setLocale(dto.getLocale());
        domain.setValue(dto.getValue());
        return domain;
    }
}
