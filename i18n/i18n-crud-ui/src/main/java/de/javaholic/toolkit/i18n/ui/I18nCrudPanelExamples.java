package de.javaholic.toolkit.i18n.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.i18n.dto.I18nEntryDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.crud.CrudPanel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class I18nCrudPanelExamples {

    private I18nCrudPanelExamples() {
    }

    public static Component entriesWithFilters(
            CrudStore<I18nEntryDto, UUID> store,
            TextResolver textResolver
    ) {
        Objects.requireNonNull(store, "store");
        Objects.requireNonNull(textResolver, "textResolver");

        FilteredI18nEntryStore filteredStore = new FilteredI18nEntryStore(store);
        CrudPanel<I18nEntryDto> panel = I18nCrudPanels.entries(filteredStore, textResolver);

        TextField keyFilter = new TextField(resolve(textResolver, "i18n.entries.filter.key", "Key contains"));
        keyFilter.setPlaceholder(resolve(textResolver, "i18n.entries.filter.key.placeholder", "e.g. app.title"));
        keyFilter.setClearButtonVisible(true);
        keyFilter.setValueChangeMode(ValueChangeMode.EAGER);
        keyFilter.addValueChangeListener(event -> {
            filteredStore.setKeyContains(event.getValue());
            panel.refresh();
        });

        ComboBox<String> localeFilter = new ComboBox<>(resolve(textResolver, "i18n.entries.filter.locale", "Locale"));
        localeFilter.setPlaceholder(resolve(textResolver, "i18n.entries.filter.locale.placeholder", "all"));
        localeFilter.setAllowCustomValue(true);
        localeFilter.setItems(loadLocales(store));
        localeFilter.addCustomValueSetListener(event -> localeFilter.setValue(event.getDetail()));
        localeFilter.addValueChangeListener(event -> {
            filteredStore.setLocale(event.getValue());
            panel.refresh();
        });

        Button clearFilters = new Button(resolve(textResolver, "i18n.entries.filter.clear", "Clear filters"), event -> {
            keyFilter.clear();
            localeFilter.clear();
        });

        HorizontalLayout filters = new HorizontalLayout(keyFilter, localeFilter, clearFilters);
        filters.setWidthFull();

        VerticalLayout layout = new VerticalLayout(filters, panel);
        layout.setSizeFull();
        layout.expand(panel);
        return layout;
    }

    private static List<String> loadLocales(CrudStore<I18nEntryDto, UUID> store) {
        return store.findAll().stream()
                .map(I18nEntryDto::getLocale)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(locale -> !locale.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    private static String resolve(TextResolver textResolver, String key, String fallback) {
        return textResolver.resolve(key).orElse(fallback);
    }

    private static final class FilteredI18nEntryStore implements CrudStore<I18nEntryDto, UUID> {
        private final CrudStore<I18nEntryDto, UUID> delegate;
        private String keyContains = "";
        private String locale = "";

        private FilteredI18nEntryStore(CrudStore<I18nEntryDto, UUID> delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<I18nEntryDto> findAll() {
            return delegate.findAll().stream()
                    .filter(this::matchesKey)
                    .filter(this::matchesLocale)
                    .toList();
        }

        @Override
        public Optional<I18nEntryDto> findById(UUID id) {
            return delegate.findById(id);
        }

        @Override
        public I18nEntryDto save(I18nEntryDto entity) {
            return delegate.save(entity);
        }

        @Override
        public void delete(I18nEntryDto entity) {
            delegate.delete(entity);
        }

        private void setKeyContains(String keyContains) {
            this.keyContains = normalize(keyContains);
        }

        private void setLocale(String locale) {
            this.locale = normalize(locale);
        }

        private boolean matchesKey(I18nEntryDto item) {
            if (keyContains.isBlank()) {
                return true;
            }
            String itemKey = normalize(item.getKey());
            return itemKey.toLowerCase().contains(keyContains.toLowerCase());
        }

        private boolean matchesLocale(I18nEntryDto item) {
            if (locale.isBlank()) {
                return true;
            }
            return normalize(item.getLocale()).equalsIgnoreCase(locale);
        }

        private String normalize(String value) {
            return value == null ? "" : value.trim();
        }
    }
}

