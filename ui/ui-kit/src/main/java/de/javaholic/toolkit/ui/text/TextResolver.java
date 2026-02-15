package de.javaholic.toolkit.ui.text;

/**
 * Resolves semantic text keys to display text in UI builders.
 *
 * <p>UiMeta stores keys only. Builders resolve those keys at render time through
 * this interface.</p>
 */
@FunctionalInterface
public interface TextResolver {

    /**
     * Resolves a semantic key to display text.
     *
     * <p>Implementations may call i18n backends, message bundles, or custom logic.</p>
     */
    String resolve(String key);
}
