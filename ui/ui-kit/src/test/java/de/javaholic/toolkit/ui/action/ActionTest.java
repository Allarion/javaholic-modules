package de.javaholic.toolkit.ui.action;

import de.javaholic.toolkit.ui.state.MutableState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActionTest {

    @Test
    void actionIsStableAfterBuild() {
        MutableState<Boolean> enabled = MutableState.of(true);

        ActionBuilder builder = Actions.create()
                .label("Save")
                .tooltip("save.tooltip")
                .enabledBy(enabled)
                .permission("user.save")
                .onClick(() -> { });

        Action action = builder.build();

        builder.label("Changed");
        enabled.set(false);

        assertThat(action.labelKeyOrText()).isEqualTo("Save");
        assertThat(action.tooltipKeyOrText()).isEqualTo("save.tooltip");
        assertThat(action.permissionKey()).contains("user.save");
        assertThat(action.enabled().get()).isFalse();
    }
}
