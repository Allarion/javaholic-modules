package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.button.Button;
import de.javaholic.toolkit.ui.action.Actions;
import de.javaholic.toolkit.ui.state.MutableState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActionBindingTest {

    @Test
    void buttonEnabledFollowsObservableValue() {
        MutableState<Boolean> enabled = MutableState.of(false);

        Actions.Action action = Actions.create()
                .label("Save")
                .enabledBy(enabled)
                .onClick(() -> { })
                .build();

        Button button = Buttons.action(action);

        assertThat(button.isEnabled()).isFalse();

        enabled.set(true);

        assertThat(button.isEnabled()).isTrue();
    }
}
