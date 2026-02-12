package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.button.Button;
import de.javaholic.toolkit.i18n.Texts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ButtonsSmokeTest {

    @Test
    void buildsButtonWithText() {
        Button button = Buttons.create()
                .text(Texts.label("ok"))
                .build();

        assertThat(button.getText()).isEqualTo("ok");
    }
}
