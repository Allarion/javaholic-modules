package de.javaholic.toolkit.i18n;

import java.util.List;
import java.util.Locale;

public final class CompositeI18n implements I18n {

    private final List<I18nProvider> providers;

    public CompositeI18n(List<I18nProvider> providers) {
        this.providers = List.copyOf(providers);
    }

    @Override
    public String resolve(String key, Locale locale) {
        for (I18nProvider p : providers) {
            if (p.contains(key)) {
                return p.get().resolve(key,locale);
            }
        }
        return "??" + key + "??";
    }
}

