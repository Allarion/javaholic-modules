package de.javaholic.toolkit.i18n;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class CompositeTextResolver implements TextResolver {

    private final List<TextResolver> providers;

    public CompositeTextResolver(List<TextResolver> providers) {
        this.providers = List.copyOf(Objects.requireNonNull(providers, "providers"));
    }

    @Override
    public Optional<String> resolve(String key, Locale locale) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(locale, "locale");
        for (TextResolver provider : providers) {
            var resolved = provider.resolve(key, locale);
            if (resolved.isPresent()) {
                return resolved;
            }
        }
        return Optional.empty();
    }
}
