package de.javaholic.toolkit.ui.form.state;

import de.javaholic.toolkit.ui.state.ObservableValue;

/**
 * Reactive form state view derived from a Vaadin Binder.
 */
public interface FormState {

    ObservableValue<Boolean> valid();

    ObservableValue<Boolean> dirty();

    ObservableValue<Boolean> submitting();

    ObservableValue<Boolean> canSubmit();

    void setSubmitting(boolean submitting);
}
