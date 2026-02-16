package de.javaholic.toolkit.i18n;

import java.util.Locale;
import java.util.Optional;

public interface I18nProvider {

    Optional<String> resolve(String key, Locale locale);
}
