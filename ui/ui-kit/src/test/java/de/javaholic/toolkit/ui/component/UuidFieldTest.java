package de.javaholic.toolkit.ui.component;

import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UuidFieldTest {

    @Test
    void setValueFormatsUuidToTextField() {
        UuidField field = new UuidField();
        UUID uuid = UUID.randomUUID();

        field.setValue(uuid);

        TextField text = (TextField) field.getChildren().findFirst().orElseThrow();
        assertThat(text.getValue()).isEqualTo(uuid.toString());
        assertThat(field.getValue()).isEqualTo(uuid);
    }

    @Test
    void invalidTextSetsInvalidStateAndNullModelValue() {
        UuidField field = new UuidField();
        TextField text = (TextField) field.getChildren().findFirst().orElseThrow();

        text.setValue("not-a-uuid");

        assertThat(field.isInvalid()).isTrue();
        assertThat(field.getValue()).isNull();
    }
}
