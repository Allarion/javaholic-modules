package de.javaholic.toolkit.i18n;

import java.util.Objects;

/**
 * Helper for prefixed i18n lookups.
 *
 * <pre>{@code
 * I18n i18n = new FileI18n(Locale.GERMAN, Path.of("i18n/messages_de.properties"));
 * I18nPrefix p = I18nPrefix.withI18n(i18n, "user.form");
 *
 * String label = p.text("name.field.label"); // user.form.name.field.label
 * }</pre>
 */
public final class I18nPrefix {

    private final I18n i18n;
    private final String prefix;

    private I18nPrefix(I18n i18n, String prefix) {
        this.i18n = i18n;
        this.prefix = prefix;
    }

    public static I18nPrefix withI18n(I18n i18n, String prefix) {
        Objects.requireNonNull(i18n, "i18n");
        Objects.requireNonNull(prefix, "prefix");
        return new I18nPrefix(i18n, prefix);
    }

    public String key(String suffix) {
        Objects.requireNonNull(suffix, "suffix");
        return prefix + "." + suffix;
    }

    public String text(String suffix) {
        return i18n.text(key(suffix));
    }
}
