package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormsOverrideTest {

    static class User {
        private String name;
    }

    @Test
    void overrideAppliesComponentLabelAndValidator() {
        TextField custom = new TextField();

        Forms.Form<User> form = Forms.of(User.class)
                .field("name", f -> {
                    f.component(custom);
                    f.label("custom.name");
                    f.validate(String.class, b -> b.withValidator(
                            value -> value != null && value.length() >= 2,
                            "too short"
                    ));
                })
                .build();

        TextField field = (TextField) form.field("name").orElseThrow();

        assertThat(field).isSameAs(custom);
        assertThat(field.getLabel()).isEqualTo("custom.name");

        form.binder().setBean(new User());
        field.setValue("A");
        assertThat(form.binder().validate().isOk()).isFalse();

        field.setValue("Anna");
        assertThat(form.binder().validate().isOk()).isTrue();
    }
}
