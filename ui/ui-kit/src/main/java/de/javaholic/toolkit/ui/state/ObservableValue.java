package de.javaholic.toolkit.ui.state;

import java.util.function.Consumer;

/**
 * Read-only observable value for UI state.
 *
 * <p>Reactive Lite contract:
 * no background threads, no schedulers, no stream graph engine.
 * Listeners are invoked in the calling thread. In Vaadin UI code this means
 * listeners should be triggered from the UI thread.</p>
 *
 * <pre>{@code
 * MutableState<Boolean> dirty = MutableState.of(false);
 * Subscription sub = dirty.subscribe(isDirty -> saveButton.setEnabled(isDirty));
 * // later: sub.unsubscribe();
 * }</pre>
 *
 * @param <T> state type
 */
public interface ObservableValue<T> {

    /**
     * Returns the current value.
     */
    T get();

    /**
     * Subscribes a listener and returns a handle for explicit unsubscribe.
     *
     * <p>The listener is called when the value changes. It is not called
     * immediately on subscription.</p>
     */
    Subscription subscribe(Consumer<T> listener);
}
