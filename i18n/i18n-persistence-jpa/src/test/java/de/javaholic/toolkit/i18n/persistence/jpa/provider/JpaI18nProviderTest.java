package de.javaholic.toolkit.i18n.persistence.jpa.provider;

import de.javaholic.toolkit.i18n.persistence.jpa.domain.I18nEntry;
import de.javaholic.toolkit.i18n.persistence.jpa.store.JpaI18nEntryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaI18nProviderTest {

    @Mock
    private JpaI18nEntryStore store;

    private JpaI18nProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JpaI18nProvider(store);
    }

    @Test
    void resolvesLocaleThenLanguageThenDefault() {
        when(store.findAll()).thenReturn(List.of(
            entry("label.ok", "", "Default"),
            entry("label.ok", "de", "Deutsch"),
            entry("label.ok", "de_DE", "Deutschland")
        ));

        Optional<String> resolved = provider.resolve("label.ok", Locale.GERMANY);

        assertThat(resolved).hasValue("Deutschland");
    }

    @Test
    void resolvesLanguageFallbackWhenCountryEntryMissing() {
        when(store.findAll()).thenReturn(List.of(
            entry("label.ok", "de", "Deutsch"),
            entry("label.ok", "", "Default")
        ));

        Optional<String> resolved = provider.resolve("label.ok", Locale.GERMANY);

        assertThat(resolved).hasValue("Deutsch");
    }

    @Test
    void returnsEmptyWhenNotFound() {
        when(store.findAll()).thenReturn(List.of(entry("another.key", "en_US", "Ignore")));

        Optional<String> resolved = provider.resolve("label.missing", Locale.US);

        assertThat(resolved).isEmpty();
    }

    private static I18nEntry entry(String key, String locale, String value) {
        return new I18nEntry(UUID.randomUUID(), key, locale, value, 0L);
    }
}
