package de.javaholic.toolkit.ui.text;

/**
 * Boundary interface for semantic UI text keys.
 *
 * <p>UI builders store keys and resolve them when applying values to Vaadin components.</p>
 */
@FunctionalInterface
public interface TextResolver {

    /**
     * Resolves the key to display text.
     */
    String resolve(String key);
}
