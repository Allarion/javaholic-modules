package de.javaholic.toolkit.ui.contract;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import de.javaholic.toolkit.ui.Buttons;
import de.javaholic.toolkit.ui.Grids;
import de.javaholic.toolkit.ui.Inputs;
import de.javaholic.toolkit.ui.form.Forms;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FluentApiContractTest {

    @Test
    void buttonsBuilderChainsAndBuildsButton() {
        Buttons.Builder builder = Buttons.create();

        Buttons.Builder chained = builder
                .label("save")
                .tooltip("save.tooltip")
                .withTextResolver(key -> key);

        Button result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(Button.class);
    }

    @Test
    void inputsBuilderChainsAndBuildsTextField() {
        Inputs.InputBuilder<TextField> builder = Inputs.textField();

        Inputs.InputBuilder<TextField> chained = builder
                .label("user.email")
                .placeholder("...");

        TextField result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(TextField.class);
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
                .withTextResolver(key -> key);

        Grid<UserDto> result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(Grid.class);
    }

    static class UserDto {
        private String email;
    }
}
