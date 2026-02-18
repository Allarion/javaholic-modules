package de.javaholic.toolkit.i18n.persistence.jpa.store;

import de.javaholic.toolkit.i18n.core.domain.I18nEntry;
import de.javaholic.toolkit.i18n.core.spi.I18nEntryStore;
import de.javaholic.toolkit.i18n.persistence.jpa.entity.JpaI18nEntry;
import de.javaholic.toolkit.i18n.persistence.jpa.mapper.JpaI18nEntryMapper;
import de.javaholic.toolkit.i18n.persistence.jpa.repo.JpaI18nEntryRepository;
import de.javaholic.toolkit.persistence.springdata.store.JpaDomainCrudStore;

import java.util.UUID;

public class JpaI18nEntryStore extends JpaDomainCrudStore<I18nEntry, UUID, JpaI18nEntry, JpaI18nEntryRepository> implements I18nEntryStore {

    public JpaI18nEntryStore(JpaI18nEntryRepository repository, JpaI18nEntryMapper mapper) {
        super(repository, mapper);
    }
}

