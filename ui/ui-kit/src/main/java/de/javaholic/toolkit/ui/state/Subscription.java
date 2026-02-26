package de.javaholic.toolkit.ui.state;

/**
 * Disposable subscription returned by {@link ObservableValue#subscribe}.
 */
@FunctionalInterface
public interface Subscription {
// TODO: check! whole state concept.
    /**
     * Removes the listener registration.
     */
    void unsubscribe();
}
