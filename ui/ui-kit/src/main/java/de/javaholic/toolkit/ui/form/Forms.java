package de.javaholic.toolkit.ui.form;

public final class Forms {

    private Forms() {
    }

    public static <T> FormBuilder<T> of(Class<T> type) {
        return new FormBuilder<>(type);
    }
}
