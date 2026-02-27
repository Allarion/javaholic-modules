package de.javaholic.toolkit.ui.resource.action;

import de.javaholic.toolkit.ui.api.ResourceAction;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Fluent builder API for creating {@link ResourceAction} instances.
 *
 * <p>This class provides a higher-level DSL for defining resource surface
 * actions. It wraps the low-level {@link ResourceAction} model and keeps
 * the public API expressive and extensible.</p>
 *
 * <p>Example:</p>
 *
 * <pre>{@code
 * ResourceActions.<User>toolbar("Create")
 *     .tooltip("user.create.tooltip")
 *     .onInvoke(context.view()::create)
 *     .build();
 * }</pre>
 */
public final class ResourceActions {

    private ResourceActions() {
    }

    /* ============================================================
       Factory methods
       ============================================================ */

    public static <T> ToolbarBuilder<T> toolbar(String label) {
        return new ToolbarBuilder<>(label);
    }

    public static <T> ItemBuilder<T> item(String label) {
        return new ItemBuilder<>(label);
    }

    public static <T> SelectionBuilder<T> selection(String label) {
        return new SelectionBuilder<>(label);
    }

    /* ============================================================
       Toolbar
       ============================================================ */

    public static final class ToolbarBuilder<T> {

        private final String label;
        private String tooltip;
        private Supplier<Boolean> enabledWhen = () -> true;
        private Runnable onInvoke;

        private ToolbarBuilder(String label) {
            this.label = Objects.requireNonNull(label, "label");
        }

        public ToolbarBuilder<T> tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public ToolbarBuilder<T> enabledWhen(Supplier<Boolean> enabledWhen) {
            this.enabledWhen = Objects.requireNonNull(enabledWhen);
            return this;
        }

        public ToolbarBuilder<T> onInvoke(Runnable onInvoke) {
            this.onInvoke = Objects.requireNonNull(onInvoke);
            return this;
        }

        public ResourceAction.ToolbarAction<T> build() {
            return new ResourceAction.ToolbarAction<>(
                    label,
                    tooltip,
                    enabledWhen,
                    Objects.requireNonNull(onInvoke, "onInvoke")
            );
        }
    }

    /* ============================================================
       Item (formerly ROW)
       ============================================================ */

    public static final class ItemBuilder<T> {

        private final String label;
        private String tooltip;
        private Predicate<T> enabledWhen = t -> true;
        private Consumer<T> onInvoke;

        private ItemBuilder(String label) {
            this.label = Objects.requireNonNull(label, "label");
        }

        public ItemBuilder<T> tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public ItemBuilder<T> enabledWhen(Predicate<T> enabledWhen) {
            this.enabledWhen = Objects.requireNonNull(enabledWhen);
            return this;
        }

        public ItemBuilder<T> onInvoke(Consumer<T> onInvoke) {
            this.onInvoke = Objects.requireNonNull(onInvoke);
            return this;
        }

        public ResourceAction.RowAction<T> build() {
            return new ResourceAction.RowAction<>(
                    label,
                    tooltip,
                    enabledWhen,
                    Objects.requireNonNull(onInvoke, "onInvoke")
            );
        }
    }

    /* ============================================================
       Selection
       ============================================================ */

    public static final class SelectionBuilder<T> {

        private final String label;
        private String tooltip;
        private Predicate<Set<T>> enabledWhen = selection -> !selection.isEmpty();
        private Consumer<Set<T>> onInvoke;

        private SelectionBuilder(String label) {
            this.label = Objects.requireNonNull(label, "label");
        }

        public SelectionBuilder<T> tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public SelectionBuilder<T> enabledWhen(Predicate<Set<T>> enabledWhen) {
            this.enabledWhen = Objects.requireNonNull(enabledWhen);
            return this;
        }

        public SelectionBuilder<T> onInvoke(Consumer<Set<T>> onInvoke) {
            this.onInvoke = Objects.requireNonNull(onInvoke);
            return this;
        }

        public ResourceAction.SelectionAction<T> build() {
            return new ResourceAction.SelectionAction<>(
                    label,
                    tooltip,
                    enabledWhen,
                    Objects.requireNonNull(onInvoke, "onInvoke")
            );
        }
    }
}