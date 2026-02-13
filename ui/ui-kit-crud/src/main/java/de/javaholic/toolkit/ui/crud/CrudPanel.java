package de.javaholic.toolkit.ui.crud;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import de.javaholic.toolkit.i18n.Texts;
import de.javaholic.toolkit.introspection.BeanIntrospector;
import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.Buttons;
import de.javaholic.toolkit.ui.Dialogs;
import de.javaholic.toolkit.ui.form.Forms;
import de.javaholic.toolkit.ui.layout.Layouts;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Minimal embeddable CRUD skeleton for Vaadin views.
 *
 * <pre>{@code
 * CrudStore<User, UUID> store = ...;
 * CrudView<User> view = CrudView.of(User.class, store);
 * add(view);
 * }</pre>
 */
public class CrudPanel<T> extends VerticalLayout {

    private final Class<T> type;
    private final BeanMeta<T> meta;
    private final CrudStore<T, ?> store;
    private final Grid<T> grid;
    private final Button createButton;
    private Supplier<Forms.FormBuilder<T>> formBuilderFactory;

    public CrudPanel(Class<T> type, CrudStore<T, ?> store) {
        this.type = Objects.requireNonNull(type, "type");
        this.meta = BeanIntrospector.inspect(type);
        this.store = Objects.requireNonNull(store, "store");
        this.grid = new Grid<>(type, false);
        // TODO: why not use Grids instead? :
        //  this.grid = Grids.of(type)
        //        .autoColumns(false) // missing feature (functionality currently resides here)
        //        .build();
        this.createButton = Buttons.create()
                .text(Texts.label("Create"))
                .build();
        this.formBuilderFactory = () -> Forms.of(type);

        configureLayout();
        configureGrid();
        configureCreateButton();
        refresh();
    }

    // TODO: fix syntax...CrudPanel.of(type).from(store).oä
    public static <T> CrudPanel<T> of(Class<T> type, CrudStore<T, ?> store) {
        return new CrudPanel<>(type, store);
    }

    public CrudPanel<T> withFormBuilderFactory(Supplier<Forms.FormBuilder<T>> formBuilderFactory) {
        this.formBuilderFactory = Objects.requireNonNull(formBuilderFactory, "formBuilderFactory");
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

    private void configureGrid() {
        Set<String> hiddenProperties = new HashSet<>();
        meta.idProperty().map(BeanProperty::name).ifPresent(hiddenProperties::add);
        meta.versionProperty().map(BeanProperty::name).ifPresent(hiddenProperties::add);

        for (BeanProperty<T, ?> property : meta.properties()) {
            if (hiddenProperties.contains(property.name())) {
                continue;
            }
            grid.addColumn(item -> readPropertyValue(property, item))
                    .setHeader(property.name());
        }

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
        Forms.Form<T> form = formBuilderFactory.get().build();
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

    @SuppressWarnings("unchecked")
    private String readPropertyValue(BeanProperty<T, ?> property, T item) {
        return String.valueOf(meta.getValue((BeanProperty<T, Object>) property, item));
    }
}
