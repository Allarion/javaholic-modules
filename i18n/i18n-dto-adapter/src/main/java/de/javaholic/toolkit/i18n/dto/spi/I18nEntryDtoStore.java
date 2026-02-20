package de.javaholic.toolkit.i18n.dto.spi;

import de.javaholic.toolkit.i18n.dto.I18nEntryDto;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

public interface I18nEntryDtoStore extends CrudStore<I18nEntryDto, UUID> {
}
