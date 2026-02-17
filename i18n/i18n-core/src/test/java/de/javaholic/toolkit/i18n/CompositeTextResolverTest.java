package de.javaholic.toolkit.i18n;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CompositeTextResolverTest {

    @Test
    void returnsFirstMatchingProviderInOrder() {
        TextResolver first = (key, locale) -> Optional.of("from-first");
        TextResolver second = (key, locale) -> Optional.of("from-second");
        CompositeTextResolver i18n = new CompositeTextResolver(List.of(first, second));

        Optional<String> resolved = i18n.resolve("label.ok", Locale.GERMANY);
        assertThat(resolved).hasValue("from-first");
    }

    @Test
    void returnsKeyWhenNoProviderResolves() {
        DefaultTextResolver defaultTextResolver = new DefaultTextResolver();
        assertThat(defaultTextResolver.resolve("label.ok", Locale.GERMANY)).hasValue("label.ok");

        TextResolver first = (key, locale) -> Optional.empty();
        assertThat(first.resolve("label.ok", Locale.GERMANY)).isNotPresent();

        TextResolver second = (key, locale) -> Optional.empty();
        assertThat(second.resolve("label.ok", Locale.GERMANY)).isNotPresent();

        CompositeTextResolver i18n = new CompositeTextResolver(List.of(first, second));
        assertThat(second.resolve("label.ok", Locale.GERMANY)).isNotPresent();

        CompositeTextResolver i18nWithDefault = new CompositeTextResolver(List.of(defaultTextResolver,first, second));
        assertThat(i18nWithDefault.resolve("label.ok",Locale.GERMANY)).hasValue("label.ok");
    }
}
