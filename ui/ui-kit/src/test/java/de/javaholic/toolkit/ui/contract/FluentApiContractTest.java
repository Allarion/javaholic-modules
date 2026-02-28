package de.javaholic.toolkit.ui.contract;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import de.javaholic.toolkit.iam.core.api.PermissionChecker;
import de.javaholic.toolkit.ui.Buttons;
import de.javaholic.toolkit.ui.Grids;
import de.javaholic.toolkit.ui.Inputs;
import de.javaholic.toolkit.ui.form.Forms;
import de.javaholic.toolkit.ui.state.MutableState;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FluentApiContractTest {

    @Test
    void buttonsBuilderChainsAndBuildsButton() {
        Buttons.Builder builder = Buttons.create();
        MutableState<Boolean> visible = MutableState.of(true);
        PermissionChecker checker = permission -> true;

        Buttons.Builder chained = builder
                .label("save")
                .tooltip("save.tooltip")
                .visibleBy(visible)
                .withPermissionChecker(checker)
                .errorNotificationMs(250)
                .withTextResolver((key, locale) -> Optional.of(key));

        Button result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(Button.class);
        assertThat(result.isVisible()).isTrue();

        visible.set(false);
        assertThat(result.isVisible()).isFalse();
    }

    @Test
    void inputsBuilderChainsAndBuildsTextField() {
        Inputs.InputBuilder<TextField> builder = Inputs.textField();
        PermissionChecker checker = permission -> false;

        Inputs.InputBuilder<TextField> chained = builder
                .label("user.email")
                .placeholder("...")
                .error("error")
                .permission("admin")
                .withTheme("small")
                .withPermissionChecker(checker);

        TextField result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(TextField.class);
        assertThat(result.getElement().getThemeList()).contains("small");
        assertThat(result.isVisible()).isFalse();
        assertThat(result.isEnabled()).isFalse();
        assertThat(result.isReadOnly()).isTrue();
    }

    @Test
    void inputsEnumSelectionBuildersBuild() {
        Select<DemoEnum> select = Inputs.select(DemoEnum.class).build();
        assertThat(select).isInstanceOf(Select.class);

        assertThat(Inputs.multiSelect(DemoEnum.class).build()).isNotNull();
        assertThat(Inputs.multiselect(String.class).build()).isNotNull();
    }

    @Test
    void formsBuilderChainsAndBuildsForm() {
        Forms.FormBuilder<UserDto> builder = Forms.of(UserDto.class);

        Forms.FormBuilder<UserDto> chained = builder
                .field("email", field -> field.label("user.email.label"));

        Forms.Form<UserDto> result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(Forms.Form.class);
    }

    @Test
    void gridsBuilderChainsAndBuildsGrid() {
        Grids.AutoGridBuilder<UserDto> builder = Grids.auto(UserDto.class);

        Grids.AutoGridBuilder<UserDto> chained = builder
                .withTextResolver((key, locale) -> Optional.of(key));

        Grid<UserDto> result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(Grid.class);
    }

    static class UserDto {
        private String email;
    }

    enum DemoEnum {
        A,
        B
    }
}
