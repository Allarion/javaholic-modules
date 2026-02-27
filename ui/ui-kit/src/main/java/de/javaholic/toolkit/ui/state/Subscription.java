package de.javaholic.toolkit.ui.state;

/**
 * Disposable subscription returned by {@link ObservableValue#subscribe}.
 */
@FunctionalInterface
public interface Subscription {
    /**
     * Removes the listener registration.
     */
    void unsubscribe();
}
