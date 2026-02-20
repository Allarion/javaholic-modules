package de.javaholic.toolkit.ui.action.vaadin;

import com.vaadin.flow.component.button.Button;
import de.javaholic.toolkit.ui.state.MutableState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VaadinActionBinderSmokeTest {

    @Test
    void bindsEnabledAndVisibleToComponent() {
        Button button = new Button("X");
        MutableState<Boolean> enabled = MutableState.of(true);
        MutableState<Boolean> visible = MutableState.of(true);

        VaadinActionBinder.bindEnabled(button, enabled);
        VaadinActionBinder.bindVisible(button, visible);

        enabled.set(false);
        visible.set(false);

        assertThat(button.isEnabled()).isFalse();
        assertThat(button.isVisible()).isFalse();
    }
}
