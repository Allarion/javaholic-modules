package de.javaholic.toolkit.ui.crud;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.crud.action.CrudAction;
import de.javaholic.toolkit.ui.crud.action.CrudPresets;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class CrudPanelActionsTest {

    @Test
    void defaultPresetRendersCreateButtonAndRowActionsColumn() {
        List<User> users = List.of(new User("A"));
        InMemoryStore<User> store = new InMemoryStore<>(users);
        Grid<User> grid = new Grid<>(User.class, false);

        CrudPanel<User> panel = CrudPanels.of(User.class)
                .withStore(store)
                .withGrid(grid)
                .build();

        assertThat(findButton(panel, "Create")).isPresent();
        assertThat(grid.getColumns()).hasSize(1);
    }

    @Test
    void nonePresetHidesDefaultCreateAndDefaultRowActionsColumn() {
        InMemoryStore<User> store = new InMemoryStore<>(List.of(new User("A")));
        Grid<User> grid = new Grid<>(User.class, false);

        CrudPanel<User> panel = CrudPanels.of(User.class)
                .withStore(store)
                .withGrid(grid)
                .preset(CrudPresets.none())
                .build();

        assertThat(findButton(panel, "Create")).isEmpty();
        assertThat(grid.getColumns()).isEmpty();
    }

    @Test
    void readOnlyPresetDisablesDefaultActionsLikeNonePreset() {
        InMemoryStore<User> store = new InMemoryStore<>(List.of(new User("A")));
        Grid<User> grid = new Grid<>(User.class, false);

        CrudPanel<User> panel = CrudPanels.of(User.class)
                .withStore(store)
                .withGrid(grid)
                .preset(CrudPresets.readOnly())
                .build();

        assertThat(findButton(panel, "Create")).isEmpty();
        assertThat(grid.getColumns()).isEmpty();
    }

    @Test
    void customToolbarAndRowActionsWorkWithNonePreset() {
        InMemoryStore<User> store = new InMemoryStore<>(List.of(new User("A")));
        Grid<User> grid = new Grid<>(User.class, false);
        AtomicInteger toolbarInvocations = new AtomicInteger();

        CrudPanel<User> panel = CrudPanels.of(User.class)
                .withStore(store)
                .withGrid(grid)
                .preset(CrudPresets.none())
                .toolbarAction(CrudAction.toolbar("Run", toolbarInvocations::incrementAndGet))
                .rowAction(CrudAction.row("Inspect", user -> { }))
                .build();

        Button run = findButton(panel, "Run").orElseThrow();
        run.click();

        assertThat(toolbarInvocations.get()).isEqualTo(1);
        assertThat(grid.getColumns()).hasSize(1);
    }

    @Test
    void toolbarActionCanBeDisabledByPredicate() {
        InMemoryStore<User> store = new InMemoryStore<>(List.of(new User("A")));
        Grid<User> grid = new Grid<>(User.class, false);

        CrudPanel<User> panel = CrudPanels.of(User.class)
                .withStore(store)
                .withGrid(grid)
                .toolbarAction(CrudAction.<User>toolbar("Run", () -> { }).enabledWhen(() -> false))
                .build();

        Button run = findButton(panel, "Run").orElseThrow();
        assertThat(run.isEnabled()).isFalse();
    }

    @Test
    void selectionActionDefaultEnablementDependsOnSelectionAndInvokesHandler() {
        User first = new User("A");
        User second = new User("B");
        InMemoryStore<User> store = new InMemoryStore<>(List.of(first, second));
        Grid<User> grid = new Grid<>(User.class, false);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        AtomicReference<Set<User>> selectedOnInvoke = new AtomicReference<>(Set.of());

        CrudPanel<User> panel = CrudPanels.of(User.class)
                .withStore(store)
                .withGrid(grid)
                .selectionAction(CrudAction.selection("Bulk", selectedOnInvoke::set))
                .build();

        Button bulk = findButton(panel, "Bulk").orElseThrow();
        assertThat(bulk.isEnabled()).isFalse();

        grid.asMultiSelect().select(first, second);

        assertThat(bulk.isEnabled()).isTrue();
        bulk.click();
        assertThat(selectedOnInvoke.get()).containsExactlyInAnyOrder(first, second);
    }

    private static Optional<Button> findButton(Component root, String text) {
        if (root instanceof Button button && text.equals(button.getText())) {
            return Optional.of(button);
        }
        return root.getChildren()
                .map(child -> findButton(child, text))
                .flatMap(Optional::stream)
                .findFirst();
    }

    private static final class InMemoryStore<T> implements CrudStore<T, Long> {
        private final List<T> entries;

        private InMemoryStore(Collection<T> entries) {
            this.entries = new ArrayList<>(entries);
        }

        @Override
        public List<T> findAll() {
            return List.copyOf(entries);
        }

        @Override
        public Optional<T> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public T save(T entity) {
            entries.remove(entity);
            entries.add(entity);
            return entity;
        }

        @Override
        public void delete(T entity) {
            entries.remove(entity);
        }
    }

    private static final class User {
        private final String name;

        private User(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof User user)) {
                return false;
            }
            return name.equals(user.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}
