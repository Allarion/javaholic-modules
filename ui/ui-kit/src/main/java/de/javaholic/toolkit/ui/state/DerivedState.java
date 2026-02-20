package de.javaholic.toolkit.ui.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Observable value derived from one or more dependencies.
 *
 * <p>Dependencies trigger recomputation. New values are emitted only when the
 * computed value changed by {@link Objects#equals(Object, Object)}.</p>
 *
 * <pre>{@code
 * MutableState<Boolean> a = MutableState.of(true);
 * MutableState<Boolean> b = MutableState.of(false);
 * ObservableValue<Boolean> both = DerivedState.of(() -> a.get() && b.get(), a, b);
 * }</pre>
 *
 * @param <T> derived value type
 */
public final class DerivedState<T> implements ObservableValue<T> {

    private final Supplier<T> compute;
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private final List<Subscription> dependencySubscriptions = new ArrayList<>();
    private T value;

    private DerivedState(Supplier<T> compute, ObservableValue<?>... deps) {
        this.compute = Objects.requireNonNull(compute, "compute");
        this.value = compute.get();
        for (ObservableValue<?> dep : deps) {
            ObservableValue<?> dependency = Objects.requireNonNull(dep, "dep");
            dependencySubscriptions.add(dependency.subscribe(any -> recompute()));
        }
    }

    public static <T> DerivedState<T> of(Supplier<T> compute, ObservableValue<?>... deps) {
        return new DerivedState<>(compute, deps);
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public Subscription subscribe(Consumer<T> listener) {
        Objects.requireNonNull(listener, "listener");
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    private void recompute() {
        T next = compute.get();
        if (Objects.equals(value, next)) {
            return;
        }
        value = next;
        List<Consumer<T>> snapshot = List.copyOf(listeners);
        for (Consumer<T> listener : snapshot) {
            listener.accept(next);
        }
    }
}
