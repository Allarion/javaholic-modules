package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.textfield.TextField;
import de.javaholic.toolkit.ui.form.state.FormState;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainValidationTest {

    static class DemoModel {
        @NotBlank
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    void notBlankBlocksSubmitUntilValueIsSet() {
        Forms.Form<DemoModel> form = Forms.of(DemoModel.class).build();
        form.binder().setBean(new DemoModel());

        FormState state = Forms.state(form.binder());

        assertThat(state.valid().get()).isFalse();
        assertThat(state.canSubmit().get()).isFalse();

        TextField name = (TextField) form.field("name").orElseThrow();
        name.setValue("Alice");

        assertThat(state.valid().get()).isTrue();
        assertThat(state.canSubmit().get()).isTrue();
    }
}
