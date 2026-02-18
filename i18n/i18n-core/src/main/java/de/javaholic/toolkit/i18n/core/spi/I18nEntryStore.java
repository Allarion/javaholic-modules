package de.javaholic.toolkit.i18n.core.spi;

import de.javaholic.toolkit.i18n.core.domain.I18nEntry;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

public interface I18nEntryStore extends CrudStore<I18nEntry, UUID> {
}
