package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.textfield.TextField;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormsRequiredMarkerTest {

    static class User {
        @NotNull
        private String name;
    }

    @Test
    void requiredMarkerIsSetFromValidationAnnotations() {
        Forms.Form<User> form = Forms.of(User.class).build();
        TextField field = (TextField) form.field("name").orElseThrow();

        assertThat(field.isRequiredIndicatorVisible()).isTrue();
    }
}
