package de.javaholic.toolkit.i18n;

import java.util.List;

public final class CompositeI18n implements I18n {

    private final List<I18nProvider> providers;

    public CompositeI18n(List<I18nProvider> providers) {
        this.providers = List.copyOf(providers);
    }

    @Override
    public String text(String key) {
        for (I18nProvider p : providers) {
            if (p.contains(key)) {
                return p.get().text(key);
            }
        }
        return "??" + key + "??";
    }
}

