package de.javaholic.toolkit.ui.api;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Action model used by the Resource UI surface for toolbar, row and selection actions.
 *
 * @param <T> grid item type
 */
public sealed interface ResourceAction<T>
        permits ResourceAction.ToolbarAction, ResourceAction.RowAction, ResourceAction.SelectionAction {

    String label();

    String tooltip();

    ResourceActionScope scope();

    static <T> ToolbarAction<T> toolbar(String label, Runnable onInvoke) {
        return new ToolbarAction<>(label, null, () -> true, onInvoke);
    }

    static <T> RowAction<T> row(String label, Consumer<T> onInvoke) {
        return new RowAction<>(label, null, item -> true, onInvoke);
    }

    /**
     * Creates a selection action.
     *
     * <p>Default enablement is {@code selection -> !selection.isEmpty()}.</p>
     */
    static <T> SelectionAction<T> selection(String label, Consumer<Set<T>> onInvoke) {
        return new SelectionAction<>(label, null, selection -> !selection.isEmpty(), onInvoke);
    }

    final class ToolbarAction<T> implements ResourceAction<T> {
        private final String label;
        private final String tooltip;
        private final Supplier<Boolean> enabledWhen;
        private final Runnable onInvoke;

        private ToolbarAction(
                String label,
                String tooltip,
                Supplier<Boolean> enabledWhen,
                Runnable onInvoke
        ) {
            this.label = Objects.requireNonNull(label, "label");
            this.tooltip = tooltip;
            this.enabledWhen = Objects.requireNonNull(enabledWhen, "enabledWhen");
            this.onInvoke = Objects.requireNonNull(onInvoke, "onInvoke");
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public String tooltip() {
            return tooltip;
        }

        @Override
        public ResourceActionScope scope() {
            return ResourceActionScope.TOOLBAR;
        }

        public Supplier<Boolean> enabledWhen() {
            return enabledWhen;
        }

        public Runnable onInvoke() {
            return onInvoke;
        }

        public ToolbarAction<T> tooltip(String tooltip) {
            return new ToolbarAction<>(label, tooltip, enabledWhen, onInvoke);
        }

        public ToolbarAction<T> enabledWhen(Supplier<Boolean> enabledWhen) {
            return new ToolbarAction<>(label, tooltip, Objects.requireNonNull(enabledWhen, "enabledWhen"), onInvoke);
        }
    }

    final class RowAction<T> implements ResourceAction<T> {
        private final String label;
        private final String tooltip;
        private final Predicate<T> enabledWhen;
        private final Consumer<T> onInvoke;

        private RowAction(
                String label,
                String tooltip,
                Predicate<T> enabledWhen,
                Consumer<T> onInvoke
        ) {
            this.label = Objects.requireNonNull(label, "label");
            this.tooltip = tooltip;
            this.enabledWhen = Objects.requireNonNull(enabledWhen, "enabledWhen");
            this.onInvoke = Objects.requireNonNull(onInvoke, "onInvoke");
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public String tooltip() {
            return tooltip;
        }

        @Override
        public ResourceActionScope scope() {
            return ResourceActionScope.ITEM;
        }

        public Predicate<T> enabledWhen() {
            return enabledWhen;
        }

        public Consumer<T> onInvoke() {
            return onInvoke;
        }

        public RowAction<T> tooltip(String tooltip) {
            return new RowAction<>(label, tooltip, enabledWhen, onInvoke);
        }

        public RowAction<T> enabledWhen(Predicate<T> enabledWhen) {
            return new RowAction<>(label, tooltip, Objects.requireNonNull(enabledWhen, "enabledWhen"), onInvoke);
        }
    }

    final class SelectionAction<T> implements ResourceAction<T> {
        private final String label;
        private final String tooltip;
        private final Predicate<Set<T>> enabledWhen;
        private final Consumer<Set<T>> onInvoke;

        private SelectionAction(
                String label,
                String tooltip,
                Predicate<Set<T>> enabledWhen,
                Consumer<Set<T>> onInvoke
        ) {
            this.label = Objects.requireNonNull(label, "label");
            this.tooltip = tooltip;
            this.enabledWhen = Objects.requireNonNull(enabledWhen, "enabledWhen");
            this.onInvoke = Objects.requireNonNull(onInvoke, "onInvoke");
        }

        @Override
        public String label() {
            return label;
        }

        @Override
        public String tooltip() {
            return tooltip;
        }

        @Override
        public ResourceActionScope scope() {
            return ResourceActionScope.SELECTION;
        }

        public Predicate<Set<T>> enabledWhen() {
            return enabledWhen;
        }

        public Consumer<Set<T>> onInvoke() {
            return onInvoke;
        }

        public SelectionAction<T> tooltip(String tooltip) {
            return new SelectionAction<>(label, tooltip, enabledWhen, onInvoke);
        }

        public SelectionAction<T> enabledWhen(Predicate<Set<T>> enabledWhen) {
            return new SelectionAction<>(label, tooltip, Objects.requireNonNull(enabledWhen, "enabledWhen"), onInvoke);
        }
    }

    /**
     * Defines where a Resource action is rendered and what context it operates on.
     */
    enum ResourceActionScope {
        /**
         * Toolbar action above the grid, independent of one specific item.
         */
        TOOLBAR,
        /**
         * Action rendered per row in the actions column, operating on one item.
         */
        ITEM,
        /**
         * Toolbar action operating on the current grid selection.
         */
        SELECTION
    }

}


