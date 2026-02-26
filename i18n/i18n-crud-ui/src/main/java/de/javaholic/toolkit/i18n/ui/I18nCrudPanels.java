package de.javaholic.toolkit.i18n.ui;

import com.vaadin.flow.component.Component;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.i18n.dto.I18nEntryDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.crud.CrudPanel;
import de.javaholic.toolkit.ui.crud.CrudPanels;

import java.util.Objects;
import java.util.UUID;

public final class I18nCrudPanels {

    // TODO v0.2: I18N-CRUD-UI: Hierarchy-aware key browser
    // TODO v0.2: I18N-CRUD-UI: locale management UI
    // TODO v0.2: I18N-CRUD-UI: multi-locale edit view
    private I18nCrudPanels() {
    }

    public static Component createView(
            CrudStore<I18nEntryDto, UUID> store,
            TextResolver textResolver
    ) {
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(textResolver, "textResolver");
        // TODO: refactor once Filter/Search is standardized in the modules
        return I18nCrudPanelExamples.entriesWithFilters(store, textResolver);
    }

    public static CrudPanel<I18nEntryDto> entries(
            CrudStore<I18nEntryDto, UUID> store,
            TextResolver textResolver
    ) {
        return CrudPanels.auto(I18nEntryDto.class)
                .withStore(store)
                .withTextResolver(textResolver)
                .preset(I18nUiPresets.texts())
                .toolbarAction(I18nUiPresets.importAction())
                .toolbarAction(I18nUiPresets.exportAction())
                .toolbarAction(I18nUiPresets.generateMissingKeysAction())
                .build();
    }
}
