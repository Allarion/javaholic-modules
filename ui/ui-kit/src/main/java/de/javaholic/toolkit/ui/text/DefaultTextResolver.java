package de.javaholic.toolkit.ui.text;

import java.util.Objects;

/**
 * Default {@link TextResolver} that falls back to returning the key itself.
 */
public final class DefaultTextResolver implements TextResolver {

    @Override
    public String resolve(String key) {
        return Objects.toString(key, "");
    }
}
