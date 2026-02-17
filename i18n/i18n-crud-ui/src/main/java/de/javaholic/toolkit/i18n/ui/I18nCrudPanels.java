package de.javaholic.toolkit.i18n.ui;

import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.i18n.ui.dto.I18nEntryDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.crud.CrudPanel;
import de.javaholic.toolkit.ui.crud.CrudPanels;

import java.util.UUID;

public final class I18nCrudPanels {

    // TODO v0.2: I18N-CRUD-UI: Hierarchy-aware key browser
    // TODO v0.2: I18N-CRUD-UI: locale management UI
    // TODO v0.2: I18N-CRUD-UI: multi-locale edit view
    private I18nCrudPanels() {
    }

    public static CrudPanel<I18nEntryDto> entries(
            CrudStore<I18nEntryDto, UUID> store,
            TextResolver textResolver
    ) {
        return CrudPanels.auto(I18nEntryDto.class)
                .withStore(store)
                .withTextResolver(textResolver)
                .build();
    }
}
