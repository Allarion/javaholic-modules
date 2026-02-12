package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.grid.Grid;
import de.javaholic.toolkit.i18n.Texts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GridsSmokeTest {

    static class User {
        private final String name;

        User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    void buildsGridWithColumns() {
        Grid<User> grid = Grids.of(User.class)
                .column(User::getName)
                    .text(Texts.label("user.name"))
                    .and()
                .build();

        assertThat(grid.getColumns()).isNotEmpty();
    }
}
