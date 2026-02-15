package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.grid.Grid;
import de.javaholic.toolkit.ui.annotations.UiHidden;
import de.javaholic.toolkit.ui.annotations.UiLabel;
import de.javaholic.toolkit.ui.annotations.UiOrder;
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

    static class AnnotatedEntity {
        @UiOrder(20)
        private String lastName;

        @UiOrder(10)
        @UiLabel(key = "user.first.label")
        private String firstName;

        @UiHidden
        private String hiddenCode;
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

    @Test
    void hiddenAnnotationHidesColumn() {
        Grid<AnnotatedEntity> grid = Grids.auto(AnnotatedEntity.class).build();

        assertThat(columnKeys(grid)).containsExactly("firstName", "lastName");
    }

    @Test
    void labelKeyIsResolvedToText() {
        Grid<AnnotatedEntity> grid = Grids.auto(AnnotatedEntity.class)
                .withTextResolver(key -> "resolved:" + key)
                .build();

        Grid.Column<AnnotatedEntity> firstNameColumn = grid.getColumns().stream()
                .filter(column -> "firstName".equals(column.getKey()))
                .findFirst()
                .orElseThrow();

        assertThat(firstNameColumn.getHeaderText()).isEqualTo("resolved:user.first.label");
    }

    @Test
    void orderAnnotationSortsColumns() {
        Grid<AnnotatedEntity> grid = Grids.auto(AnnotatedEntity.class).build();

        assertThat(columnKeys(grid)).containsExactly("firstName", "lastName");
    }

    private static List<String> columnKeys(Grid<?> grid) {
        return grid.getColumns().stream()
                .map(Grid.Column::getKey)
                .toList();
    }
}
