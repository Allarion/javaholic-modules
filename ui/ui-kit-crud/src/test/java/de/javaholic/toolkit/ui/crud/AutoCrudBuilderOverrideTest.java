package de.javaholic.toolkit.ui.crud;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.grid.Grid;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.form.Forms;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutoCrudBuilderOverrideTest {

    @Test
    void overrideLabelIsAppliedToAutoGridColumn() throws Exception {
        CrudPanel<UserDto> panel = CrudPanels.auto(UserDto.class)
                .withStore(new StubCrudStore<>())
                .withTextResolver((key,locale) -> Optional.of("resolved:" + key))
                .override("email", config -> config.label("user.email.label"))
                .build();

        Grid<UserDto> grid = gridOf(panel);
        Grid.Column<UserDto> emailColumn = grid.getColumns().stream()
                .filter(column -> "email".equals(column.getKey()))
                .findFirst()
                .orElseThrow();

        assertThat(emailColumn.getHeaderText()).isEqualTo("resolved:user.email.label");
    }

    @Test
    void overrideUnknownPropertyFailsClearly() {
        assertThatThrownBy(() -> CrudPanels.auto(UserDto.class)
                .withStore(new StubCrudStore<>())
                .override("doesNotExist", config -> config.label("x"))
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown property override");
    }

    @Test
    void overrideRequiredAndTooltipAreAppliedToAutoFormField() throws Exception {
        CrudPanel<UserDto> panel = CrudPanels.auto(UserDto.class)
                .withStore(new StubCrudStore<>())
                .withTextResolver((key,locale) -> Optional.of("resolved:" + key))
                .override("email", config -> config
                        .required(true)
                        .tooltip("user.email.tooltip"))
                .build();

        Forms.Form<UserDto> form = formFactoryOf(panel).get();
        Component component = form.field("email").orElseThrow();

        assertThat(component).isInstanceOf(HasValueAndElement.class);
        HasValueAndElement<?, ?> field = (HasValueAndElement<?, ?>) component;
        assertThat(field.isRequiredIndicatorVisible()).isTrue();
        assertThat(component.getElement().getProperty("title"))
                .isEqualTo("resolved:user.email.tooltip");
    }

    @SuppressWarnings("unchecked")
    private static <T> Grid<T> gridOf(CrudPanel<T> panel) throws Exception {
        Field field = CrudPanel.class.getDeclaredField("grid");
        field.setAccessible(true);
        return (Grid<T>) field.get(panel);
    }

    @SuppressWarnings("unchecked")
    private static <T> Supplier<Forms.Form<T>> formFactoryOf(CrudPanel<T> panel) throws Exception {
        Field field = CrudPanel.class.getDeclaredField("formFactory");
        field.setAccessible(true);
        return (Supplier<Forms.Form<T>>) field.get(panel);
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
            // no-op
        }
    }

    static class UserDto {
        private String email;
    }
}
