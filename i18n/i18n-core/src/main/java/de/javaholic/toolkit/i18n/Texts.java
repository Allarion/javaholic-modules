package de.javaholic.toolkit.i18n;

import java.util.Objects;

public final class Texts {

    private Texts() {
    }

    public static Text label(String key) {
        return new Text(TextRole.LABEL, key);
    }

    public static Text header(String key) {
        return new Text(TextRole.HEADER, key);
    }

    public static Text description(String key) {
        return new Text(TextRole.DESCRIPTION, key);
    }

    public static Text error(String key) {
        return new Text(TextRole.ERROR, key);
    }

    public static Text tooltip(String key) {
        return new Text(TextRole.TOOLTIP, key);
    }

    public static String resolve(I18n i18n, Text text) {
        Objects.requireNonNull(text, "text");
        return i18n != null ? i18n.text(text.key()) : text.key();
    }
}
