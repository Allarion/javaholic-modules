package de.javaholic.toolkit.i18n.ui;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.i18n.dto.I18nEntryDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class I18nResourcePanelsSmokeTest {

    @Mock
    private CrudStore<I18nEntryDto, UUID> store;

    private static final TextResolver RESOLVER = (key, locale) -> Optional.of(key);

    @Test
    void entriesPanelCreatesAndLoadsItems() {
        when(store.findAll()).thenReturn(List.of(new I18nEntryDto("app.title", "en", "App")));

        var panel = I18nResourcePanels.entries(store, RESOLVER);

        assertThat(panel).isNotNull();
        verify(store, atLeastOnce()).findAll();
    }

    @Test
    void entriesWithFiltersCreatesFilterBarAndRefreshes() {
        when(store.findAll()).thenReturn(List.of(
                new I18nEntryDto("app.title", "en", "App"),
                new I18nEntryDto("app.title", "de", "Anwendung")
        ));

        var component = I18nResourcePanelExamples.entriesWithFilters(store, RESOLVER);

        assertThat(component).isInstanceOf(VerticalLayout.class);
        VerticalLayout root = (VerticalLayout) component;
        HorizontalLayout filters = (HorizontalLayout) root.getComponentAt(0);
        TextField keyFilter = (TextField) filters.getComponentAt(0);
        keyFilter.setValue("app.");

        verify(store, atLeastOnce()).findAll();
    }
}


