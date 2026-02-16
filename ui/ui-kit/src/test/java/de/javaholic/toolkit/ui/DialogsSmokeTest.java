package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.grid.Grid;

import de.javaholic.toolkit.i18n.TextResolver;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;

class DialogsSmokeTest {

    static class User {
        private String name;
    }

    @Test
    void confirmAndSelectBuildersDoNotThrow() {
        TextResolver resolver = (key, locale) -> Optional.of("i18n:" + key);
        Grid<User> grid = new Grid<>(User.class, false);

        assertThatCode(() -> Dialogs.confirm()
                .withTextResolver(resolver)
                .header("confirm.title")
                .confirmLabel("ok")
                .cancelLabel("cancel"))
                .doesNotThrowAnyException();

        assertThatCode(() -> Dialogs.select(grid)
                .withTextResolver(resolver)
                .header("select.title")
                .confirmLabel("ok")
                .cancelLabel("cancel"))
                .doesNotThrowAnyException();
    }
}

