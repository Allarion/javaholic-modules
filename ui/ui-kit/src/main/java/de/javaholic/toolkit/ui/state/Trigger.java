package de.javaholic.toolkit.ui.state;

import java.util.function.Consumer;

/**
 * Manual reactive trigger.
 *
 * <p>Each {@link #fire()} increments the counter value and notifies listeners.
 * Use this as an explicit dependency for {@link DerivedState} when you want
 * pull-style recomputation.</p>
 *
 * <pre>{@code
 * Trigger t = new Trigger();
 * ObservableValue<Boolean> valid = DerivedState.of(() -> binder.isValid(), t);
 * binder.addStatusChangeListener(e -> t.fire());
 * }</pre>
 */
public final class Trigger implements ObservableValue<Long> {

    private final MutableState<Long> state = MutableState.of(0L);

    public void fire() {
        state.update(current -> current + 1L);
    }

    @Override
    public Long get() {
        return state.get();
    }

    @Override
    public Subscription subscribe(Consumer<Long> listener) {
        return state.subscribe(listener);
    }
}
