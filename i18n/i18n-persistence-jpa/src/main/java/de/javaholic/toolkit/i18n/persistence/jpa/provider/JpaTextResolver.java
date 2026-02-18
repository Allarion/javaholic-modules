package de.javaholic.toolkit.i18n.persistence.jpa.provider;

import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.i18n.core.domain.I18nEntry;
import de.javaholic.toolkit.i18n.core.spi.I18nEntryStore;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class JpaTextResolver implements TextResolver {

    private final I18nEntryStore store;
    private final Locale defaultLocale;

    public JpaTextResolver(I18nEntryStore store) {
        this(store, Locale.ROOT);
    }

    public JpaTextResolver(I18nEntryStore store, Locale defaultLocale) {
        this.store = Objects.requireNonNull(store, "store");
        this.defaultLocale = Objects.requireNonNull(defaultLocale, "defaultLocale");
    }

    @Override
    public Optional<String> resolve(String key, Locale locale) {
        Objects.requireNonNull(key, "key");
        Locale requested = locale != null ? locale : defaultLocale;

        for (String localeKey : fallbackChain(requested, defaultLocale)) {
            Optional<String> match = store.findAll().stream()
                .filter(entry -> Objects.equals(key, entry.getKey()) && Objects.equals(localeKey, entry.getLocale()))
                .map(I18nEntry::getValue)
                .findFirst();
            if (match.isPresent()) {
                return match;
            }
        }
        return Optional.empty();
    }

    private static Set<String> fallbackChain(Locale requested, Locale defaultLocale) {
        Set<String> locales = new LinkedHashSet<>();
        if (!requested.toString().isBlank()) {
            locales.add(requested.toString());
        }
        if (!requested.getLanguage().isBlank()) {
            locales.add(requested.getLanguage());
        }
        if (!defaultLocale.toString().isBlank()) {
            locales.add(defaultLocale.toString());
        }
        locales.add("");
        return locales;
    }
}

