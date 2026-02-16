package de.javaholic.toolkit.i18n;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class EditableI18nProvider implements I18nProvider {

    private final Map<String, Map<String, String>> entries = new ConcurrentHashMap<>();

    public void put(String key, Locale locale, String value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(value, "value");
        entries.computeIfAbsent(key, ignored -> new ConcurrentHashMap<>())
            .put(toLocaleKey(locale), value);
    }

    @Override
    public Optional<String> resolve(String key, Locale locale) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(locale, "locale");
        Map<String, String> byLocale = entries.get(key);
        if (byLocale == null) {
            return Optional.empty();
        }
        for (String localeKey : candidateLocales(locale)) {
            String resolved = byLocale.get(localeKey);
            if (resolved != null) {
                return Optional.of(resolved);
            }
        }
        return Optional.empty();
    }

    private static Set<String> candidateLocales(Locale locale) {
        Set<String> result = new LinkedHashSet<>();
        if (!locale.toString().isBlank()) {
            result.add(toLocaleKey(locale));
        }
        if (!locale.getLanguage().isBlank()) {
            result.add(locale.getLanguage());
        }
        result.add("");
        return result;
    }

    private static String toLocaleKey(Locale locale) {
        if (locale == Locale.ROOT) {
            return "";
        }
        return locale.toString();
    }
}
