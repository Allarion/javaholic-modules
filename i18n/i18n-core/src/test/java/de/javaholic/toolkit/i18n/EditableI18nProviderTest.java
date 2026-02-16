package de.javaholic.toolkit.i18n;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class EditableI18nProviderTest {

    @Test
    void resolvesInMemoryEntriesWithFallback() {
        EditableI18nProvider provider = new EditableI18nProvider();
        provider.put("title", Locale.ROOT, "Default");
        provider.put("title", Locale.GERMAN, "Deutsch");
        provider.put("title", Locale.GERMANY, "Deutschland");

        assertThat(provider.resolve("title", Locale.GERMANY)).hasValue("Deutschland");
        assertThat(provider.resolve("title", Locale.GERMAN)).hasValue("Deutsch");
        assertThat(provider.resolve("title", Locale.US)).hasValue("Default");
    }
}
