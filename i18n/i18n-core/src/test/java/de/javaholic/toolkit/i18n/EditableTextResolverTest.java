package de.javaholic.toolkit.i18n;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class EditableTextResolverTest {

    @Test
    void resolvesInMemoryEntriesWithFallback() {
        EditableTextResolver provider = new EditableTextResolver();
        provider.put("title", Locale.ROOT, "Default");
        provider.put("title", Locale.GERMAN, "Deutsch");
        provider.put("title", Locale.GERMANY, "Deutschland");

        assertThat(provider.resolve("title", Locale.GERMANY)).hasValue("Deutschland");
        assertThat(provider.resolve("title", Locale.GERMAN)).hasValue("Deutsch");
        assertThat(provider.resolve("title", Locale.US)).hasValue("Default");
    }
}
