package de.javaholic.toolkit.ui.resource;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.Buttons;
import de.javaholic.toolkit.ui.Dialogs;
import de.javaholic.toolkit.ui.resource.action.ResourceAction;
import de.javaholic.toolkit.ui.resource.action.ResourcePreset;
import de.javaholic.toolkit.ui.form.Forms;
import de.javaholic.toolkit.ui.layout.Layouts;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Embeddable Resource UI surface for Vaadin views.
 *
 * <p>Internal term: DAF means "Dataset + Actions + Forms" (work title). This
 * component is the concrete DAF surface orchestration.</p>
 *
 * <p>Responsibility: orchestrate store operations, grid refresh, and form dialog lifecycle.</p>
 *
 * <p>Must not do: decide metadata/rendering policy for fields/columns.
 * Rendering decisions belong to dedicated builders and factories.</p>
 *
 * <p>Architecture fit: top-level orchestration layer. It composes existing UI toolkit parts and
 * persistence abstractions ({@link CrudStore}) without redefining lower-layer rules.</p>
 *
 *
 * <p>Usage:</p>
 * <pre>{@code
 * CrudStore<User, UUID> store = ...;
 * ResourcePanel<User> panel = ResourcePanels.auto(User.class)
 *         .withStore(store)
 *         .build();
 * add(panel);
 * }</pre>
 *
 * <p>Concept: this component coordinates existing building blocks
 * ({@link Grid}, {@link Forms.Form}, {@link CrudStore}) and keeps each concern isolated:
 * layout/dialog flow here, mapping/persistence in stores, and field/column metadata in builders.</p>
 */
public final class ResourcePanel<T> extends VerticalLayout {
// TODO: Since Actions are now not necessary CRUD the class name isn't fitting. ActionsPanel o.ä. (also revisit other Crud Classes + package name)
    private final Class<T> type;
    private final CrudStore<T, ?> store;
    private final Grid<T> grid;
    private final Supplier<Forms.Form<T>> formFactory;
    private final ResourcePreset preset;
    private final List<ResourceAction.ToolbarAction<T>> toolbarActions;
    private final List<ResourceAction.RowAction<T>> rowActions;
    private final List<ResourceAction.SelectionAction<T>> selectionActions;
    private final List<SelectionActionBinding<T>> selectionActionBindings = new ArrayList<>();

    /**
     * Creates a Resource panel for one type and backing store.
     *
     * <p>Use {@link ResourcePanels} builders instead of calling this constructor directly.</p>
     */
    ResourcePanel(
            Class<T> type,
            CrudStore<T, ?> store,
            Grid<T> grid,
            Supplier<Forms.Form<T>> formFactory,
            ResourcePreset preset,
            List<ResourceAction.ToolbarAction<T>> toolbarActions,
            List<ResourceAction.RowAction<T>> rowActions,
            List<ResourceAction.SelectionAction<T>> selectionActions
    ) {
        this.type = Objects.requireNonNull(type, "type");
        this.store = Objects.requireNonNull(store, "store");
        this.grid = Objects.requireNonNull(grid, "grid");
        this.formFactory = Objects.requireNonNull(formFactory, "formFactory");
        this.preset = Objects.requireNonNull(preset, "preset");
        this.toolbarActions = List.copyOf(Objects.requireNonNull(toolbarActions, "toolbarActions"));
        this.rowActions = List.copyOf(Objects.requireNonNull(rowActions, "rowActions"));
        this.selectionActions = List.copyOf(Objects.requireNonNull(selectionActions, "selectionActions"));

        addActionsColumnIfNeeded();
        configureLayout();
        configureSelectionActions();
        refresh();
    }

    /**
     * Reloads all rows from the store into the grid.
     *
     * <p>Example: {@code panel.refresh();}</p>
     */
    public void refresh() {
        // TODO: add paging/filtering/sorting support for large datasets.
        List<T> items = store.findAll();
        grid.setItems(items);
        updateSelectionActionEnablement();
    }

    private void configureLayout() {
        setSizeFull();
        grid.setSizeFull();
        add(buildToolbar(), grid);
        expand(grid);
    }

    /**
     * Composes the toolbar from preset defaults and custom toolbar/selection actions.
     */
    private HorizontalLayout buildToolbar() {
        List<Button> buttons = new ArrayList<>();
        if (preset.enableCreate()) {
            Button createButton = Buttons.create()
                    .label("Create")
                    .action(this::onCreate)
                    .build();
            buttons.add(createButton);
        }

        for (ResourceAction.ToolbarAction<T> action : toolbarActions) {
            Button button = createButton(action.label(), action.tooltip(), action.variants(), action.onInvoke());
            button.setEnabled(Boolean.TRUE.equals(action.enabledWhen().get()));
            buttons.add(button);
        }

        for (ResourceAction.SelectionAction<T> action : selectionActions) {
            Button button = createButton(
                    action.label(),
                    action.tooltip(),
                    action.variants(),
                    () -> action.onInvoke().accept(currentSelection())
            );
            selectionActionBindings.add(new SelectionActionBinding<>(action, button));
            buttons.add(button);
        }

        return Layouts.hbox(buttons.toArray(Button[]::new));
    }

    /**
     * Adds the row actions column only when default row actions or custom row actions exist.
     */
    private void addActionsColumnIfNeeded() {
        if (!preset.enableEdit() && !preset.enableDelete() && rowActions.isEmpty()) {
            return;
        }

        grid.addColumn(new ComponentRenderer<>(item -> {
                    List<Button> buttons = new ArrayList<>();
                    if (preset.enableEdit()) {
                        buttons.add(Buttons.create()
                                .label("Edit")
                                .style(ButtonVariant.LUMO_TERTIARY_INLINE)
                                .action(() -> onEdit(item))
                                .build());
                    }
                    if (preset.enableDelete()) {
                        buttons.add(Buttons.create()
                                .label("Delete")
                                .style(ButtonVariant.LUMO_ERROR)
                                .style(ButtonVariant.LUMO_TERTIARY_INLINE)
                                .action(() -> onDelete(item))
                                .build());
                    }
                    for (ResourceAction.RowAction<T> action : rowActions) {
                        Button button = createButton(
                                action.label(),
                                action.tooltip(),
                                action.variants(),
                                () -> action.onInvoke().accept(item)
                        );
                        button.setEnabled(action.enabledWhen().test(item));
                        buttons.add(button);
                    }
                    return Layouts.hbox(buttons.toArray(Button[]::new));
                }))
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);
    }

    /**
     * Selection actions are rendered as toolbar buttons and re-evaluated on selection changes.
     */
    private void configureSelectionActions() {
        if (selectionActions.isEmpty()) {
            return;
        }
        grid.addSelectionListener(event -> updateSelectionActionEnablement());
        updateSelectionActionEnablement();
    }

    /**
     * Recomputes selection-action enablement from each action predicate and current selection.
     */
    private void updateSelectionActionEnablement() {
        Set<T> selection = currentSelection();
        selectionActionBindings.forEach(binding ->
                binding.button().setEnabled(binding.action().enabledWhen().test(selection))
        );
    }

    private Set<T> currentSelection() {
        return Set.copyOf(new LinkedHashSet<>(grid.getSelectedItems()));
    }

    private Button createButton(String label, String tooltip, List<ButtonVariant> variants, Runnable invoke) {
        Buttons.Builder builder = Buttons.create()
                .label(label)
                .action(invoke);
        if (tooltip != null && !tooltip.isBlank()) {
            builder.tooltip(tooltip);
        }
        variants.forEach(builder::style);
        return builder.build();
    }

    /**
     * Opens the create dialog with a fresh bean instance.
     *
     * <p>Example: subclass and call {@code super.onCreate();} or override for custom prefill.</p>
     */
    protected void onCreate() {
        T bean = newEmptyBean();
        openFormDialog("Create " + type.getSimpleName(), bean);
    }

    /**
     * Opens the edit dialog for the selected item.
     *
     * <p>Example: override to add audit logging before delegating.</p>
     */
    protected void onEdit(T item) {
        openFormDialog("Edit " + type.getSimpleName(), item);
    }

    /**
     * Opens a confirmation dialog and deletes on confirmation.
     *
     * <p>Concept: this is the extension point for soft-delete strategies.</p>
     */
    protected void onDelete(T item) {
        Dialogs.confirm()
                .header("Delete " + type.getSimpleName())
                .description("Really delete this item?")
                .confirmLabel("Delete")
                .cancelLabel("cancel")
                .open(confirmed -> {
                    if (!confirmed) {
                        return;
                    }
                    // TODO: add SoftDelete Support; dont forget its also a tech-field
                    deleteAndRefresh(item);
                });
    }

    private void openFormDialog(String title, T bean) {
        Forms.Form<T> form = formFactory.get();
        form.binder().setBean(bean);

        Dialogs.form(form)
                .header(title)
                .confirmLabel("Save")
                .cancelLabel("cancel")
                .onOk(f -> saveAndRefresh(f.binder().getBean()))
                .open();
    }

    // TODO:add supplier as alternative to newEmptyBean: .withNewInstanceSupplier(Supplier<T>)
    private T newEmptyBean() {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Type " + type.getName() + " must declare an accessible no-args constructor for create dialogs.",
                    e
            );
        }
    }

    /**
     * Persists one bean and reloads the grid.
     */
    void saveAndRefresh(T bean) {
        store.save(bean);
        refresh();
    }

    /**
     * Deletes one bean and reloads the grid.
     */
    void deleteAndRefresh(T bean) {
        store.delete(bean);
        refresh();
    }

    private record SelectionActionBinding<T>(ResourceAction.SelectionAction<T> action, Button button) {
    }
}

