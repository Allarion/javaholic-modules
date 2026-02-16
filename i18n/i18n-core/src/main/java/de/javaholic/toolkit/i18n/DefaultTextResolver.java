package de.javaholic.toolkit.i18n;

import java.util.Locale;
import java.util.Optional;

/**
 * Default resolver that returns keys unchanged.
 */
public final class DefaultTextResolver implements TextResolver {

    @Override
    public Optional<String> resolve(String key, Locale locale) {
        return Optional.of(key);
    }
}
