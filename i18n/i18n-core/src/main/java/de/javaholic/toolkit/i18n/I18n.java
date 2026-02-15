package de.javaholic.toolkit.i18n;

/**
 * Minimal lookup-only i18n service.
 *
 * <p>Usage:</p>
 * <pre>{@code
 * I18n i18n = new CompositeI18n(List.of(
 *     new FileI18nProvider("messages_de.properties"),
 *     new FileI18nProvider("messages.properties") // fallback
 * ));
 * String title = i18n.text("user.create.dialog.title");
 * }</pre>
 */
public interface I18n {
    String text(String key);
}


// TODO; I1N8 UI Feature:  @UILabel Scanning.