package de.javaholic.toolkit.ui.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.javaholic.toolkit.ui.Buttons;
import de.javaholic.toolkit.ui.action.Action;
import de.javaholic.toolkit.ui.state.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fluent helper for composing Vaadin layouts and action containers.
 *
 * <p>Reactive Lite lifecycle rule: no background thread orchestration.
 * Subscriptions created by menu/toolbar builders are disposed on detach.</p>
 */
public final class Layouts {

    private Layouts() {
    }

    public static VerticalLayout vbox(Component... components) {
        var box = new VerticalLayout();
        box.add(components);
        box.addClassNames("box", "vertical");
        return box;
    }

    public static HorizontalLayout hbox(Component... components) {
        var box = new HorizontalLayout();
        box.add(components);
        box.addClassNames("box", "horizontal");
        return box;
    }

    public static Component spacer() {
        var spacer = new Div();
        spacer.addClassName("spacer");
        return spacer;
    }

    /**
     * Builds a menu from immutable actions.
     *
     * <pre>{@code
     * Component menu = Layouts.menu()
     *     .item(saveAction)
     *     .separator()
     *     .item(deleteAction)
     *     .build();
     * }</pre>
     */
    public static MenuBuilder menu() {
        return new MenuBuilder();
    }

    /**
     * Builds a horizontal toolbar with actions and custom components.
     *
     * <pre>{@code
     * HorizontalLayout toolbar = Layouts.toolbar()
     *     .action(saveAction)
     *     .spacer()
     *     .action(deleteAction)
     *     .build();
     * }</pre>
     */
    public static ToolbarBuilder toolbar() {
        return new ToolbarBuilder();
    }

    public static final class MenuBuilder {

        private final List<MenuEntry> entries = new ArrayList<>();

        public MenuBuilder item(Action action) {
            entries.add(MenuEntry.action(Objects.requireNonNull(action, "action")));
            return this;
        }

        public MenuBuilder separator() {
            entries.add(MenuEntry.separator());
            return this;
        }

        public Component build() {
            MenuBar menuBar = new MenuBar();
            MenuBinding binding = new MenuBinding(menuBar, List.copyOf(entries));
            binding.rebuild();
            menuBar.addDetachListener(event -> binding.dispose());
            return menuBar;
        }
    }

    public static final class ToolbarBuilder {

        private final List<ToolbarEntry> entries = new ArrayList<>();
        private boolean overflowToMenu;

        public ToolbarBuilder action(Action action) {
            entries.add(ToolbarEntry.action(Objects.requireNonNull(action, "action")));
            return this;
        }

        public ToolbarBuilder component(Component component) {
            entries.add(ToolbarEntry.component(Objects.requireNonNull(component, "component")));
            return this;
        }

        public ToolbarBuilder spacer() {
            entries.add(ToolbarEntry.spacer());
            return this;
        }

        /**
         * API placeholder. Overflow routing is intentionally not implemented yet.
         */
        public ToolbarBuilder overflowToMenu(boolean overflowToMenu) {
            this.overflowToMenu = overflowToMenu;
            return this;
        }

        public HorizontalLayout build() {
            HorizontalLayout toolbar = hbox();
            toolbar.addClassName("toolbar");
            if (overflowToMenu) {
                toolbar.getElement().setAttribute("data-overflow", "menu-todo");
            }
            for (ToolbarEntry entry : entries) {
                switch (entry.kind) {
                    case ACTION -> toolbar.add(Buttons.from(entry.action));
                    case COMPONENT -> toolbar.add(entry.component);
                    case SPACER -> toolbar.add(Layouts.spacer());
                }
            }
            return toolbar;
        }
    }

    private static final class MenuBinding {

        private final MenuBar menuBar;
        private final List<MenuEntry> entries;
        private final List<Subscription> subscriptions = new ArrayList<>();

        private MenuBinding(MenuBar menuBar, List<MenuEntry> entries) {
            this.menuBar = menuBar;
            this.entries = entries;
        }

        private void rebuild() {
            dispose();
            menuBar.removeAll();

            for (MenuEntry entry : entries) {
                if (entry.separator) {
                    MenuItem separator = menuBar.addItem("|");
                    separator.setEnabled(false);
                    separator.getElement().setAttribute("aria-hidden", "true");
                    continue;
                }

                Action action = entry.action;
                if (!Boolean.TRUE.equals(action.visible().get())) {
                    subscribeVisibility(action);
                    continue;
                }

                MenuItem item = menuBar.addItem(action.labelKeyOrText(), e -> action.onClick().run());
                if (action.tooltipKeyOrText() != null) {
                    item.getElement().setProperty("title", action.tooltipKeyOrText());
                }
                item.setEnabled(Boolean.TRUE.equals(action.enabled().get()));

                subscriptions.add(action.enabled().subscribe(v ->
                        applyMenuItemEnabled(item, Boolean.TRUE.equals(v))
                ));
                subscribeVisibility(action);
            }
        }

        private void subscribeVisibility(Action action) {
            subscriptions.add(action.visible().subscribe(v -> rebuild()));
        }

        private void applyMenuItemEnabled(MenuItem item, boolean enabled) {
            item.setEnabled(enabled);
        }

        private void dispose() {
            List<Subscription> snapshot = List.copyOf(subscriptions);
            subscriptions.clear();
            snapshot.forEach(Subscription::unsubscribe);
        }
    }

    private static final class MenuEntry {

        private final Action action;
        private final boolean separator;

        private MenuEntry(Action action, boolean separator) {
            this.action = action;
            this.separator = separator;
        }

        private static MenuEntry action(Action action) {
            return new MenuEntry(action, false);
        }

        private static MenuEntry separator() {
            return new MenuEntry(null, true);
        }
    }

    private static final class ToolbarEntry {

        private final Kind kind;
        private final Action action;
        private final Component component;

        private ToolbarEntry(Kind kind, Action action, Component component) {
            this.kind = kind;
            this.action = action;
            this.component = component;
        }

        private static ToolbarEntry action(Action action) {
            return new ToolbarEntry(Kind.ACTION, action, null);
        }

        private static ToolbarEntry component(Component component) {
            return new ToolbarEntry(Kind.COMPONENT, null, component);
        }

        private static ToolbarEntry spacer() {
            return new ToolbarEntry(Kind.SPACER, null, null);
        }

        private enum Kind {
            ACTION,
            COMPONENT,
            SPACER
        }
    }
}
