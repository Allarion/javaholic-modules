package de.javaholic.toolkit.ui.resource;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.Buttons;
import de.javaholic.toolkit.ui.action.Actions;
import de.javaholic.toolkit.ui.form.Forms;
import de.javaholic.toolkit.ui.form.state.FormState;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ResourcePanelValidationTest {

    @Test
    void saveActionIsDisabledWhenInvalidAndEnabledWhenValid() {
        Forms.Form<Model> form = Forms.of(Model.class).build();

        ResourcePanels.of(Model.class)
                .withStore(new StubCrudStore<>())
                .withForm(form)
                .build();

        form.binder().setBean(new Model());
        FormState state = Forms.state(form.binder());

        Button save = Buttons.action(Actions.create()
                .label("Save")
                .enabledBy(state.canSubmit())
                .onClick(() -> { })
                .build());

        assertThat(save.isEnabled()).isFalse();

        TextField name = (TextField) form.field("name").orElseThrow();
        name.setValue("ok");

        assertThat(save.isEnabled()).isTrue();
    }

    static class Model {
        @NotBlank
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static final class StubCrudStore<T> implements CrudStore<T, Long> {
        @Override
        public List<T> findAll() {
            return List.of();
        }

        @Override
        public Optional<T> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public T save(T entity) {
            return entity;
        }

        @Override
        public void delete(T entity) {
        }
    }
}

