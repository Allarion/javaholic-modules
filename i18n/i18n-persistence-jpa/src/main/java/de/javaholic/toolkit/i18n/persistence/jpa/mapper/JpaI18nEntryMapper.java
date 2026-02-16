package de.javaholic.toolkit.i18n.persistence.jpa.mapper;

import de.javaholic.toolkit.i18n.persistence.jpa.domain.I18nEntry;
import de.javaholic.toolkit.i18n.persistence.jpa.entity.JpaI18nEntry;
import de.javaholic.toolkit.persistence.core.EntityMapper;

public class JpaI18nEntryMapper implements EntityMapper<I18nEntry, JpaI18nEntry> {

    @Override
    public I18nEntry toDomain(JpaI18nEntry entity) {
        if (entity == null) {
            return null;
        }
        return new I18nEntry(
            entity.getId(),
            entity.getKey(),
            entity.getLocale(),
            entity.getValue(),
            entity.getVersion()
        );
    }

    @Override
    public JpaI18nEntry toEntity(I18nEntry domain) {
        if (domain == null) {
            return null;
        }
        return new JpaI18nEntry(
            domain.getId(),
            domain.getKey(),
            domain.getLocale(),
            domain.getValue(),
            domain.getVersion()
        );
    }
}
