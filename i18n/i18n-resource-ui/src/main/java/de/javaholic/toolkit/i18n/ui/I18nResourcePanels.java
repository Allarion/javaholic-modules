package de.javaholic.toolkit.i18n.ui;

import com.vaadin.flow.component.Component;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.i18n.dto.I18nEntryDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.resource.GridFormsResourceView;
import de.javaholic.toolkit.ui.resource.ResourcePanels;
import de.javaholic.toolkit.ui.resource.actionprovider.CrudActionProvider;

import java.util.Objects;
import java.util.UUID;

public final class I18nResourcePanels {

    // TODO v0.2: I18N-CRUD-UI: Hierarchy-aware key browser
    // TODO v0.2: I18N-CRUD-UI: locale management UI
    // TODO v0.2: I18N-CRUD-UI: multi-locale edit view
    private I18nResourcePanels() {
    }

    public static Component createView(
            CrudStore<I18nEntryDto, UUID> store,
            TextResolver textResolver
    ) {
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(textResolver, "textResolver");
        // TODO: refactor once Filter/Search is standardized in the modules
        return I18nResourcePanelExamples.entriesWithFilters(store, textResolver);
    }

    public static GridFormsResourceView<I18nEntryDto> entries(
            CrudStore<I18nEntryDto, UUID> store,
            TextResolver textResolver
    ) {
        return ResourcePanels.auto(I18nEntryDto.class)
                .withStore(store)
                .withTextResolver(textResolver)
                .withActionProvider(CrudActionProvider.class)
                .action(I18nUiActions.importAction())
                .action(I18nUiActions.exportAction())
                .action(I18nUiActions.generateMissingKeysAction())
                .build();
    }
}
