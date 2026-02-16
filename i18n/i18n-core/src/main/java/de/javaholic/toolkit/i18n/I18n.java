package de.javaholic.toolkit.i18n;

import java.util.Locale;

/**
 * Minimal lookup-only i18n service.
 *
 * <p>Usage:</p>
 * <pre>{@code
 * I18n i18n = new CompositeI18n(List.of(
 *     new LocalFileI18nProvider("messages_de.properties"),
 *     new LocalFileI18nProvider("messages.properties") // fallback
 * ));
 * String title = i18n.resolve("user.create.dialog.title", Locale.DE);
 * }</pre>
 *
 */
public interface I18n {
   String resolve(String key, Locale locale);
}


// TODO; I1N8 UI Feature:  @UILabel Scanning.
