package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.textfield.TextField;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormsAutoTest {

    static class Entity {
        @Id
        private long id;
        private String name;
        @Version
        private long version;
        private String email;
    }

    @Test
    void autoHidesIdAndVersionByDefault() {
        Forms.Form<Entity> form = Forms.auto(Entity.class).build();

        assertThat(form.field("id")).isEmpty();
        assertThat(form.field("version")).isEmpty();
        assertThat(form.field("name")).isPresent();
        assertThat(form.field("email")).isPresent();
    }

    @Test
    void autoExcludeRemovesConfiguredProperties() {
        Forms.Form<Entity> form = Forms.auto(Entity.class)
                .exclude("email")
                .build();

        assertThat(form.field("name")).isPresent();
        assertThat(form.field("email")).isEmpty();
    }

    @Test
    void autoOverrideIsApplied() {
        Forms.Form<Entity> form = Forms.auto(Entity.class)
                .override("name", field -> ((TextField) field).setLabel("Custom Name"))
                .build();

        TextField field = (TextField) form.field("name").orElseThrow();
        assertThat(field.getLabel()).isEqualTo("Custom Name");
    }
}
