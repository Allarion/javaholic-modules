package de.javaholic.toolkit.ui.component;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.UUID;

public class UuidField extends CustomField<UUID> {

    private static final String DEFAULT_ERROR_MESSAGE = "Invalid UUID";

    private final TextField textField = new TextField();

    public UuidField() {
        add(textField);
        textField.addValueChangeListener(event -> updateValidationState(event.getValue()));
    }

    @Override
    protected UUID generateModelValue() {
        String raw = textField.getValue();
        if (raw == null || raw.isBlank()) {
            updateValidationState(raw);
            return null;
        }
        try {
            UUID value = UUID.fromString(raw.trim());
            updateValidationState(raw);
            return value;
        } catch (IllegalArgumentException ignored) {
            updateValidationState(raw);
            return null;
        }
    }

    @Override
    protected void setPresentationValue(UUID uuid) {
        textField.setValue(uuid == null ? "" : uuid.toString());
        updateValidationState(textField.getValue());
    }

    @Override
    public void setInvalid(boolean invalid) {
        super.setInvalid(invalid);
        textField.setInvalid(invalid);
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        super.setErrorMessage(errorMessage);
        textField.setErrorMessage(errorMessage);
    }

    private void updateValidationState(String raw) {
        if (raw == null || raw.isBlank()) {
            setInvalid(false);
            return;
        }
        try {
            UUID.fromString(raw.trim());
            setInvalid(false);
        } catch (IllegalArgumentException ignored) {
            if (getErrorMessage() == null || getErrorMessage().isBlank()) {
                setErrorMessage(DEFAULT_ERROR_MESSAGE);
            }
            setInvalid(true);
        }
    }
}
