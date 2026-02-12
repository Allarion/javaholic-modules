package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FormsBuildTest {

    static class User {
        private String first;
        private String last;
    }

    record UserRecord(String first, String last) {
    }

    static class ValidatedUser {
        @NotBlank
        private String name;
    }

    @Test
    void buildCreatesLayoutBinderAndFields() {
        Forms.Form<User> form = Forms.of(User.class).build();

        assertThat(form.layout()).isNotNull();
        assertThat(form.binder()).isNotNull();
        assertThat(form.field("first")).isPresent();
        assertThat(form.field("last")).isPresent();
    }

    @Test
    void fieldOrderPreservedForPojo() {
        Forms.Form<User> form = Forms.of(User.class).build();
        VerticalLayout layout = (VerticalLayout) form.layout();

        List<String> labels = layout.getChildren()
                .filter(component -> component instanceof HasLabel)
                .map(component -> ((HasLabel) component).getLabel())
                .toList();

        assertThat(labels).containsExactly("first", "last");
    }

    @Test
    void fieldOrderPreservedForRecord() {
        Forms.Form<UserRecord> form = Forms.of(UserRecord.class).build();
        VerticalLayout layout = (VerticalLayout) form.layout();

        List<String> labels = layout.getChildren()
                .filter(component -> component instanceof HasLabel)
                .map(component -> ((HasLabel) component).getLabel())
                .toList();

        assertThat(labels).containsExactly("first", "last");
    }

    @Test
    void beanValidationFailsForBlankValue() {
        Forms.Form<ValidatedUser> form = Forms.of(ValidatedUser.class).build();
        form.binder().setBean(new ValidatedUser());

        assertThat(form.binder().validate().isOk()).isFalse();

        Component nameField = form.field("name").orElseThrow();
        assertThat(nameField).isInstanceOf(com.vaadin.flow.component.textfield.TextField.class);
        ((com.vaadin.flow.component.textfield.TextField) nameField).setValue("ok");

        assertThat(form.binder().validate().isOk()).isTrue();
    }
}
