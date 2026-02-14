package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.grid.Grid;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GridsAutoTest {

    static class Entity {
        @Id
        private long id;
        private String name;
        @Version
        private long version;
        private String email;
    }

    @Test
    void autoHidesIdAndVersionByDefault() {
        Grid<Entity> grid = Grids.auto(Entity.class).build();

        assertThat(columnKeys(grid)).containsExactly("name", "email");
    }

    @Test
    void autoExcludeRemovesConfiguredProperties() {
        Grid<Entity> grid = Grids.auto(Entity.class)
                .exclude("email")
                .build();

        assertThat(columnKeys(grid)).containsExactly("name");
    }

    @Test
    void autoOverrideIsApplied() {
        Grid<Entity> grid = Grids.auto(Entity.class)
                .override("name", column -> column.setWidth("321px"))
                .build();

        Grid.Column<Entity> nameColumn = grid.getColumns().stream()
                .filter(column -> "name".equals(column.getKey()))
                .findFirst()
                .orElseThrow();

        assertThat(nameColumn.getWidth()).isEqualTo("321px");
    }

    private static List<String> columnKeys(Grid<Entity> grid) {
        return grid.getColumns().stream()
                .map(Grid.Column::getKey)
                .toList();
    }
}
