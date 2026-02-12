package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.grid.Grid;
import de.javaholic.toolkit.i18n.I18n;
import de.javaholic.toolkit.i18n.Texts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class DialogsSmokeTest {

    static class User {
        private String name;
    }

    @Test
    void confirmAndSelectBuildersDoNotThrow() {
        I18n i18n = key -> "i18n:" + key;
        Grid<User> grid = new Grid<>(User.class, false);

        assertThatCode(() -> Dialogs.confirm()
                .withI18n(i18n)
                .text(Texts.header("confirm.title"))
                .textConfirm(Texts.label("ok"))
                .textCancel(Texts.label("cancel")))
                .doesNotThrowAnyException();

        assertThatCode(() -> Dialogs.select(grid)
                .withI18n(i18n)
                .text(Texts.header("select.title"))
                .textConfirm(Texts.label("ok"))
                .textCancel(Texts.label("cancel")))
                .doesNotThrowAnyException();
    }
}
