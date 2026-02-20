package de.javaholic.toolkit.ui.state;

import java.util.Objects;

/**
 * Boolean helpers for Reactive Lite state composition.
 *
 * <pre>{@code
 * ObservableValue<Boolean> canSave = BooleanStates.and(
 *     BooleanStates.isTrue(formValid),
 *     BooleanStates.not(BooleanStates.isTrue(busy))
 * );
 * }</pre>
 */
public final class BooleanStates {

    private static final ObservableValue<Boolean> TRUE = new ConstantBooleanState(true);
    private static final ObservableValue<Boolean> FALSE = new ConstantBooleanState(false);

    private BooleanStates() {
    }

    @SafeVarargs
    public static ObservableValue<Boolean> and(ObservableValue<Boolean>... values) {
        return DerivedState.of(() -> {
            for (ObservableValue<Boolean> value : values) {
                if (!Boolean.TRUE.equals(value.get())) {
                    return false;
                }
            }
            return true;
        }, values);
    }

    @SafeVarargs
    public static ObservableValue<Boolean> or(ObservableValue<Boolean>... values) {
        return DerivedState.of(() -> {
            for (ObservableValue<Boolean> value : values) {
                if (Boolean.TRUE.equals(value.get())) {
                    return true;
                }
            }
            return false;
        }, values);
    }

    public static ObservableValue<Boolean> not(ObservableValue<Boolean> value) {
        ObservableValue<Boolean> source = Objects.requireNonNull(value, "value");
        return DerivedState.of(() -> !Boolean.TRUE.equals(source.get()), source);
    }

    public static ObservableValue<Boolean> isTrue(ObservableValue<Boolean> value) {
        ObservableValue<Boolean> source = Objects.requireNonNull(value, "value");
        return DerivedState.of(() -> Boolean.TRUE.equals(source.get()), source);
    }

    public static ObservableValue<Boolean> constant(boolean value) {
        return value ? TRUE : FALSE;
    }

    private static final class ConstantBooleanState implements ObservableValue<Boolean> {

        private final boolean value;

        private ConstantBooleanState(boolean value) {
            this.value = value;
        }

        @Override
        public Boolean get() {
            return value;
        }

        @Override
        public Subscription subscribe(java.util.function.Consumer<Boolean> listener) {
            return () -> { };
        }
    }
}
