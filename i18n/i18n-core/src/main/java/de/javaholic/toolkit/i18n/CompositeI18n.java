package de.javaholic.toolkit.i18n;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class CompositeI18n implements I18n {

    private final List<I18nProvider> providers;

    public CompositeI18n(List<I18nProvider> providers) {
        this.providers = List.copyOf(Objects.requireNonNull(providers, "providers"));
    }

    @Override
    public String resolve(String key, Locale locale) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(locale, "locale");
        for (I18nProvider provider : providers) {
            var resolved = provider.resolve(key, locale);
            if (resolved.isPresent()) {
                return resolved.get();
            }
        }
        return key;
    }
}
