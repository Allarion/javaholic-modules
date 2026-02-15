package de.javaholic.toolkit.ui.text;

/**
 * Default resolver that returns keys unchanged.
 */
public final class DefaultTextResolver implements TextResolver {

    @Override
    public String resolve(String key) {
        return key;
    }
}
