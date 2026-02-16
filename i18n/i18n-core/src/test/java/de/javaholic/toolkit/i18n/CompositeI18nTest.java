package de.javaholic.toolkit.i18n;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CompositeI18nTest {

    @Test
    void returnsFirstMatchingProviderInOrder() {
        I18nProvider first = (key, locale) -> Optional.of("from-first");
        I18nProvider second = (key, locale) -> Optional.of("from-second");
        CompositeI18n i18n = new CompositeI18n(List.of(first, second));

        String resolved = i18n.resolve("label.ok", Locale.GERMANY);

        assertThat(resolved).isEqualTo("from-first");
    }

    @Test
    void returnsKeyWhenNoProviderResolves() {
        I18nProvider first = (key, locale) -> Optional.empty();
        I18nProvider second = (key, locale) -> Optional.empty();
        CompositeI18n i18n = new CompositeI18n(List.of(first, second));

        String resolved = i18n.resolve("label.cancel", Locale.US);

        assertThat(resolved).isEqualTo("label.cancel");
    }
}
