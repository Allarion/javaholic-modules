package de.javaholic.toolkit.ui.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Mutable state holder used for simple UI reactivity.
 *
 * <p>Designed for Vaadin UI usage: update from UI event listeners and bind to
 * components via {@code ObservableValue} subscriptions.</p>
 *
 * <pre>{@code
 * MutableState<String> name = MutableState.of("");
 * name.subscribe(value -> preview.setText(value));
 * name.set("Alice");
 * }</pre>
 *
 * @param <T> value type
 */
public final class MutableState<T> implements ObservableValue<T> {

    private final List<Consumer<T>> listeners = new ArrayList<>();
    private T value;

    private MutableState(T initial) {
        this.value = initial;
    }

    public static <T> MutableState<T> of(T initial) {
        return new MutableState<>(initial);
    }

    @Override
    public T get() {
        return value;
    }

    /**
     * Sets a new value and notifies listeners when it changed.
     */
    public void set(T value) {
        if (Objects.equals(this.value, value)) {
            return;
        }
        this.value = value;
        notifyListeners(value);
    }

    /**
     * Updates current value with a function.
     */
    public void update(UnaryOperator<T> updater) {
        set(Objects.requireNonNull(updater, "updater").apply(value));
    }

    @Override
    public Subscription subscribe(Consumer<T> listener) {
        Objects.requireNonNull(listener, "listener");
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    private void notifyListeners(T next) {
        List<Consumer<T>> snapshot = List.copyOf(listeners);
        for (Consumer<T> listener : snapshot) {
            listener.accept(next);
        }
    }
}
