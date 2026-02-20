package de.javaholic.toolkit.i18n.dto.store;

import de.javaholic.toolkit.i18n.core.domain.I18nEntry;
import de.javaholic.toolkit.i18n.dto.I18nEntryDto;
import de.javaholic.toolkit.i18n.dto.spi.I18nEntryDtoStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.DtoCrudStore;
import de.javaholic.toolkit.persistence.core.DtoMapper;

import java.util.UUID;

public class I18nEntryDtoCrudStore extends DtoCrudStore<I18nEntryDto, I18nEntry, UUID> implements I18nEntryDtoStore {

    public I18nEntryDtoCrudStore(
            CrudStore<I18nEntry, UUID> delegate,
            DtoMapper<I18nEntryDto, I18nEntry> mapper
    ) {
        super(delegate, mapper);
    }
}
