package de.javaholic.toolkit.i18n;

import java.util.Locale;
import java.util.Optional;

/**
     *
     * interface for semantic UI text keys.
     *  *
     *  * <p>UI builders store keys and resolve them when applying values to Vaadin components.</p>
     *
     * Minimal lookup-only i18n service.
     *
     * <p>Usage:</p>
     * <pre>{@code
     * TextResolver i18n = new CompositeI18n(List.of(
     *     new LocalFileI18nProvider("messages_de.properties"),
     *     new LocalFileI18nProvider("messages.properties") // fallback
     * ));
     * String title = i18nresolve("user.create.dialog.title", Locale.DE);
     * }</pre>
     *
     */
    @FunctionalInterface
    public interface TextResolver {
        default Optional<String> resolve(String key) {
            return resolve(key, Locale.getDefault());
        }

        Optional<String> resolve(String key, Locale locale);
    }