package de.javaholic.toolkit.ui.crud.action;

import com.vaadin.flow.component.button.ButtonVariant;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Action model used by CRUD panels for toolbar, row and selection actions.
 *
 * @param <T> grid item type
 */
public sealed interface CrudAction<T>
        permits CrudAction.ToolbarAction, CrudAction.RowAction, CrudAction.SelectionAction {

    String label();

    String tooltip();

    List<ButtonVariant> variants();

    CrudActionScope scope();

    static <T> ToolbarAction<T> toolbar(String label, Runnable onInvoke) {
        return new ToolbarAction<>(label, null, List.of(), () -> true, onInvoke);
    }

    static <T> RowAction<T> row(String label, Consumer<T> onInvoke) {
        return new RowAction<>(label, null, List.of(), item -> true, onInvoke);
    }

    /**
     * Creates a selection action.
     *
     * <p>Default enablement is {@code selection -> !selection.isEmpty()}.</p>
     */
    static <T> SelectionAction<T> selection(String label, Consumer<Set<T>> onInvoke) {
        return new SelectionAction<>(label, null, List.of(), selection -> !selection.isEmpty(), onInvoke);
    }

    final class ToolbarAction<T> implements CrudAction<T> {
        private final String label;
        private final String tooltip;
        private final List<ButtonVariant> variants;
        private final Supplier<Boolean> enabledWhen;
        private final Runnable onInvoke;

        private ToolbarAction(
                String label,
                String tooltip,
                List<ButtonVariant> variants,
                Supplier<Boolean> enabledWhen,
                Runnable onInvoke
        ) {
            this.label = Objects.requireNonNull(label, "label");
            this.tooltip = tooltip;
            this.variants = List.copyOf(Objects.requireNonNull(variants, "variants"));
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
        public List<ButtonVariant> variants() {
            return variants;
        }

        @Override
        public CrudActionScope scope() {
            return CrudActionScope.TOOLBAR;
        }

        public Supplier<Boolean> enabledWhen() {
            return enabledWhen;
        }

        public Runnable onInvoke() {
            return onInvoke;
        }

        public ToolbarAction<T> tooltip(String tooltip) {
            return new ToolbarAction<>(label, tooltip, variants, enabledWhen, onInvoke);
        }

        public ToolbarAction<T> variants(ButtonVariant... variants) {
            return new ToolbarAction<>(label, tooltip, List.copyOf(Arrays.asList(Objects.requireNonNull(variants, "variants"))), enabledWhen, onInvoke);
        }

        public ToolbarAction<T> enabledWhen(Supplier<Boolean> enabledWhen) {
            return new ToolbarAction<>(label, tooltip, variants, Objects.requireNonNull(enabledWhen, "enabledWhen"), onInvoke);
        }
    }

    final class RowAction<T> implements CrudAction<T> {
        private final String label;
        private final String tooltip;
        private final List<ButtonVariant> variants;
        private final Predicate<T> enabledWhen;
        private final Consumer<T> onInvoke;

        private RowAction(
                String label,
                String tooltip,
                List<ButtonVariant> variants,
                Predicate<T> enabledWhen,
                Consumer<T> onInvoke
        ) {
            this.label = Objects.requireNonNull(label, "label");
            this.tooltip = tooltip;
            this.variants = List.copyOf(Objects.requireNonNull(variants, "variants"));
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
        public List<ButtonVariant> variants() {
            return variants;
        }

        @Override
        public CrudActionScope scope() {
            return CrudActionScope.ROW;
        }

        public Predicate<T> enabledWhen() {
            return enabledWhen;
        }

        public Consumer<T> onInvoke() {
            return onInvoke;
        }

        public RowAction<T> tooltip(String tooltip) {
            return new RowAction<>(label, tooltip, variants, enabledWhen, onInvoke);
        }

        public RowAction<T> variants(ButtonVariant... variants) {
            return new RowAction<>(label, tooltip, List.copyOf(Arrays.asList(Objects.requireNonNull(variants, "variants"))), enabledWhen, onInvoke);
        }

        public RowAction<T> enabledWhen(Predicate<T> enabledWhen) {
            return new RowAction<>(label, tooltip, variants, Objects.requireNonNull(enabledWhen, "enabledWhen"), onInvoke);
        }
    }

    final class SelectionAction<T> implements CrudAction<T> {
        private final String label;
        private final String tooltip;
        private final List<ButtonVariant> variants;
        private final Predicate<Set<T>> enabledWhen;
        private final Consumer<Set<T>> onInvoke;

        private SelectionAction(
                String label,
                String tooltip,
                List<ButtonVariant> variants,
                Predicate<Set<T>> enabledWhen,
                Consumer<Set<T>> onInvoke
        ) {
            this.label = Objects.requireNonNull(label, "label");
            this.tooltip = tooltip;
            this.variants = List.copyOf(Objects.requireNonNull(variants, "variants"));
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
        public List<ButtonVariant> variants() {
            return variants;
        }

        @Override
        public CrudActionScope scope() {
            return CrudActionScope.SELECTION;
        }

        public Predicate<Set<T>> enabledWhen() {
            return enabledWhen;
        }

        public Consumer<Set<T>> onInvoke() {
            return onInvoke;
        }

        public SelectionAction<T> tooltip(String tooltip) {
            return new SelectionAction<>(label, tooltip, variants, enabledWhen, onInvoke);
        }

        public SelectionAction<T> variants(ButtonVariant... variants) {
            return new SelectionAction<>(label, tooltip, List.copyOf(Arrays.asList(Objects.requireNonNull(variants, "variants"))), enabledWhen, onInvoke);
        }

        public SelectionAction<T> enabledWhen(Predicate<Set<T>> enabledWhen) {
            return new SelectionAction<>(label, tooltip, variants, Objects.requireNonNull(enabledWhen, "enabledWhen"), onInvoke);
        }
    }
}
