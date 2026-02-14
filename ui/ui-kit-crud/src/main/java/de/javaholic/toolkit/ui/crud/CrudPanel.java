package de.javaholic.toolkit.ui.crud;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import de.javaholic.toolkit.i18n.Texts;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.Buttons;
import de.javaholic.toolkit.ui.Dialogs;
import de.javaholic.toolkit.ui.Grids;
import de.javaholic.toolkit.ui.form.Forms;
import de.javaholic.toolkit.ui.layout.Layouts;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Embeddable CRUD composition panel for Vaadin views.
 *
 * <p>Responsibility: orchestrate store operations, grid refresh, and form dialog lifecycle.</p>
 *
 * <p>Must not do: decide metadata/rendering policy for fields/columns.
 * Rendering decisions belong to {@link Grids} and {@link Forms} builders.</p>
 *
 * <p>Architecture fit: top-level orchestration layer. It composes existing UI toolkit parts and
 * persistence abstractions ({@link CrudStore}) without redefining lower-layer rules.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * CrudStore<User, UUID> store = ...;
 * CrudPanel<User> panel = CrudPanel.of(User.class, store);
 * add(panel);
 * }</pre>
 */
public class CrudPanel<T> extends VerticalLayout {

    private final Class<T> type;
    private final CrudStore<T, ?> store;
    private final Grid<T> grid;
    private final Button createButton;
    private Supplier<Forms.Form<T>> formFactory;

    public CrudPanel(Class<T> type, CrudStore<T, ?> store) {
        this.type = Objects.requireNonNull(type, "type");
        this.store = Objects.requireNonNull(store, "store");
        this.grid = Grids.auto(type)
                .configure(this::addActionsColumn)
                .build();
        this.createButton = Buttons.create()
                .text(Texts.label("Create"))
                .build();
        this.formFactory = () -> Forms.auto(type).build();

        configureLayout();
        configureCreateButton();
        refresh();
    }


    // TODO: fix syntax...CrudPanel.of(type).from(store).oä
    public static <T> CrudPanel<T> of(Class<T> type, CrudStore<T, ?> store) {
        return new CrudPanel<>(type, store);
    }

    public CrudPanel<T> withFormBuilderFactory(Supplier<Forms.FormBuilder<T>> formBuilderFactory) {
        Objects.requireNonNull(formBuilderFactory, "formBuilderFactory");
        this.formFactory = () -> formBuilderFactory.get().build();
        return this;
    }

    public void refresh() {
        // TODO: add paging/filtering/sorting support for large datasets.
        List<T> items = store.findAll();
        grid.setItems(items);
    }

    private void configureLayout() {
        setSizeFull();
        grid.setSizeFull();
        add(createButton, grid);
        expand(grid);
    }
    // TODO: gehört eher ins Grids? vll so: Grids.auto(type).withActions(...) ?
    private void addActionsColumn(Grid<T> grid) {
        // TODO: add optional selection dialog integration when use-cases need it.
        grid.addColumn(new ComponentRenderer<>(item -> {
                    Button edit = Buttons.create()
                            .text(Texts.label("Edit"))
                            .style(ButtonVariant.LUMO_TERTIARY_INLINE)
                            .action(() -> onEdit(item))
                            .build();
                    Button delete = Buttons.create()
                            .text(Texts.label("Delete"))
                            .style(ButtonVariant.LUMO_ERROR)
                            .style(ButtonVariant.LUMO_TERTIARY_INLINE)
                            .action(() -> onDelete(item))
                            .build();
                    return Layouts.hbox(edit, delete);
                }))
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);
    }

    private void configureCreateButton() {
        createButton.addClickListener(event -> onCreate());
    }

    protected void onCreate() {
        T bean = newEmptyBean();
        openFormDialog("Create " + type.getSimpleName(), bean);
    }

    protected void onEdit(T item) {
        openFormDialog("Edit " + type.getSimpleName(), item);
    }

    protected void onDelete(T item) {
        Dialogs.confirm()
                .text(Texts.header("Delete " + type.getSimpleName()))
                .text(Texts.description("Really delete this item?"))
                .textConfirm(Texts.label("Delete"))
                .textCancel()
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
                .text(Texts.header(title))
                .textConfirm(Texts.label("Save"))
                .textCancel()
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

    void saveAndRefresh(T bean) {
        store.save(bean);
        refresh();
    }

    void deleteAndRefresh(T bean) {
        store.delete(bean);
        refresh();
    }
}
