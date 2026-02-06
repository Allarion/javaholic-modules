package de.javaholic.toolkit.i18n;

/**
 * Minimal lookup-only i18n service.
 *
 * <p>Usage:</p>
 * <pre>{@code
 * I18n i18n = new FileI18n(Locale.GERMAN, Path.of("i18n/messages_de.properties"));
 * String title = i18n.text("user.create.dialog.title");
 * }</pre>
 */
public interface I18n {
    String text(String key);
}
