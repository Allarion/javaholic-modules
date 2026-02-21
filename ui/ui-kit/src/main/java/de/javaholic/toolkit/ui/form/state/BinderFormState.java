package de.javaholic.toolkit.ui.form.state;

import com.vaadin.flow.data.binder.Binder;
import de.javaholic.toolkit.ui.state.BooleanStates;
import de.javaholic.toolkit.ui.state.MutableState;
import de.javaholic.toolkit.ui.state.ObservableValue;

import java.util.Objects;

/**
 * Binder-backed {@link FormState} implementation.
 *
 * <p>Uses Binder status/value change events and does not call
 * {@code binder.validate()} from listeners.</p>
 */
public class BinderFormState<T> implements FormState {

    private final MutableState<Boolean> valid = MutableState.of(false);
    private final MutableState<Boolean> dirty = MutableState.of(false);
    private final MutableState<Boolean> submitting = MutableState.of(false);
    private final ObservableValue<Boolean> canSubmit;

    public BinderFormState(Binder<T> binder) {
        Binder<T> source = Objects.requireNonNull(binder, "binder");

        valid.set(source.isValid());
        dirty.set(source.hasChanges());

        source.addStatusChangeListener(event -> valid.set(source.isValid()));
        binder.addValueChangeListener(e -> {
            valid.set(binder.isValid());
            dirty.set(binder.hasChanges());
        });
        this.canSubmit = BooleanStates.and(valid, BooleanStates.not(submitting));
    }

    @Override
    public ObservableValue<Boolean> valid() {
        return valid;
    }

    @Override
    public ObservableValue<Boolean> dirty() {
        return dirty;
    }

    @Override
    public ObservableValue<Boolean> submitting() {
        return submitting;
    }

    @Override
    public ObservableValue<Boolean> canSubmit() {
        return canSubmit;
    }

    @Override
    public void setSubmitting(boolean submitting) {
        this.submitting.set(submitting);
    }
}
