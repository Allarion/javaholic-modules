package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.grid.Grid;

import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.ui.form.Forms;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DialogsSmokeTest {

    static class User {
        private String name;
    }

    static class FormModel {
        private String name;
    }

    @Test
    void confirmAndSelectBuildersDoNotThrow() {
        TextResolver resolver = (key, locale) -> Optional.of("i18n:" + key);
        Grid<User> grid = new Grid<>(User.class, false);

        assertThatCode(() -> Dialogs.confirm()
                .withTextResolver(resolver)
                .header("confirm.title")
                .description("confirm.description")
                .confirmLabel("ok")
                .confirmTooltip("confirm.tooltip")
                .cancelLabel("cancel"))
                .doesNotThrowAnyException();
        assertThatCode(() -> Dialogs.confirm()
                .confirmLabel("ok")
                .cancelTooltip("cancel.tooltip"))
                .doesNotThrowAnyException();

        assertThatCode(() -> Dialogs.select(grid)
                .withTextResolver(resolver)
                .header("select.title")
                .description("select.description")
                .confirmLabel("ok")
                .confirmTooltip("confirm.tooltip")
                .cancelLabel("cancel"))
                .doesNotThrowAnyException();
        assertThatCode(() -> Dialogs.select(grid)
                .confirmLabel("ok")
                .cancelTooltip("cancel.tooltip")
                .withContent())
                .doesNotThrowAnyException();
    }

    @Test
    void openMethodsRemainGuardedByConfirmLabel() {
        Grid<User> grid = new Grid<>(User.class, false);

        assertThatThrownBy(() -> Dialogs.confirm().open(confirmed -> { }))
                .isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> Dialogs.select(grid).open(selected -> { }))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void formDialogBuilderChainsAndGuardsOpenWithoutConfirmLabel() {
        Forms.Form<FormModel> form = Forms.of(FormModel.class).build();

        assertThatCode(() -> Dialogs.form(form)
                .description("desc")
                .confirmLabel("ok")
                .confirmTooltip("confirm.tooltip")
                .cancelLabel("cancel")
                .cancelTooltip("cancel.tooltip")
                .onOk((f, dialog) -> dialog.close()))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> Dialogs.form(form).open())
                .isInstanceOf(IllegalStateException.class);
    }
}

