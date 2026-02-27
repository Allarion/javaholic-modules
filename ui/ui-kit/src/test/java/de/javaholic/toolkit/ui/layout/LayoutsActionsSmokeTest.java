package de.javaholic.toolkit.ui.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.javaholic.toolkit.ui.action.Actions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LayoutsActionsSmokeTest {

    @Test
    void menuAndToolbarBuildWithActions() {
        Actions.Action save = Actions.create().label("Save").onClick(() -> { }).build();
        Actions.Action delete = Actions.create().label("Delete").onClick(() -> { }).build();

        Component menu = Layouts.menu().item(save).separator().item(delete).build();
        HorizontalLayout toolbar = Layouts.toolbar().action(save).spacer().action(delete).build();

        assertThat(menu).isNotNull();
        assertThat(toolbar.getComponentCount()).isEqualTo(3);
    }
}
