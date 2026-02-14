package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import de.javaholic.toolkit.i18n.I18n;
import de.javaholic.toolkit.i18n.Text;
import de.javaholic.toolkit.i18n.TextRole;
import de.javaholic.toolkit.i18n.Texts;
import de.javaholic.toolkit.ui.component.UnGroupedRadioButton;
import de.javaholic.toolkit.ui.meta.UiInspector;
import de.javaholic.toolkit.ui.meta.UiMeta;
import de.javaholic.toolkit.ui.meta.UiProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// TODO: Aaron: look into: AUTO-GRID; CRUD (PAID VAADIN)
/**
 * Fluent builder for {@link Grid} configuration.<br>
 *
 * <p>Example:</p>
 *
 * <pre>{@code
 * Grids.of(User.class)
 *     .items(users)
 *     .column(User::getUsername)
 *         .text(Texts.label("user.username"))
 *         .and()
 *     .column(new ComponentRenderer<>(user -> new Icon("vaadin", "user")))
 *         .text(Texts.label("user.icon"))
 *         .and()
 *     .build();
 * }</pre>
 */
public final class Grids {

    private Grids() {
    }

    /**
     * Fluent start point<br>
     *
     * <p>Example:</p>
     *
     * <pre>{@code
     * Grid<User> grid =
     *     Grids.of(User.class)
     *     .build();
     *
     * }</pre>
     *
     * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
     */
    public static <T> GridBuilder<T> of(Class<T> type) {
        return new GridBuilder<>(type);
    }

    /**
     * Fluent start point for a convention-based grid using {@link UiMeta}.
     *
     * <p>Visible properties are derived from {@link UiInspector#inspect(Class)} and can be
     * adjusted via {@link AutoGridBuilder#exclude(String...)} and
     * {@link AutoGridBuilder#override(String, Consumer)}.</p>
     */
    public static <T> AutoGridBuilder<T> auto(Class<T> type) {
        return new AutoGridBuilder<>(type);
    }

    public static class GridBuilder<T> {

        private final Grid<T> grid;
        private I18n i18n;

        private GridBuilder(Class<T> type) {
            this.grid = new Grid<>(type, false);
            grid.setSizeFull();
        }


        /**
         * Fluent End point.<br>
         * <br>
         * Creates a grid for displaying {@code User} objects.
         *
         * <p>Example:</p>
         *
         * <pre>{@code
         * Grid<User> grid =
         *     Grids.of(User.class)
         *          .column(User::getId).text(Texts.label("user.id")).and()
         *          .column(User::getName).text(Texts.label("user.name")).and()
         *          .build();
         * }</pre>
         *
         * @return {@code Grid<T>}
         */
        public Grid<T> build() {
            return grid;
        }

        /**
         * sets {@link Collection} as grid items
         *
         * <p>Example:</p>
         *
         * <pre>{@code
         * Grids.of(Foo.class)
         *     .items(list)
         *     .column(Foo::getName);
         * }</pre>
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> items(Collection<T> items) {
            return items(() -> items);
        }

        /**
         * uses the {@link Supplier} to setItems on grid
         *
         * <p>Example:</p>
         *
         * <pre>{@code
         * Grids.of(Foo.class)
         *     .items(() -> service.loadFoos())
         *     .column(Foo::getName);
         * }</pre>
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> items(Supplier<? extends Collection<T>> getter) {
            grid.setItems(getter.get());
            return this;
        }

        /**
         * registers the given {@link DataProvider}
         *
         * <p>Example:</p>
         *
         * <pre>{@code
         * DataProvider<Foo, SearchFilter> dp =
         *     DataProvider.fromFilteringCallbacks(...)
         *         .withConfigurableFilter();
         *
         * Grids.of(Foo.class)
         *     .items(dp)
         *     .column(Foo::getName);
         * }</pre>
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> items(DataProvider<T, ?> provider) {
            grid.setDataProvider(provider);
            return this;
        }

        /**
         * Adds a column using a {@link ValueProvider}.
         *
         * <p>Example:</p>
         *
         * <pre>{@code
         * Grid<User> grid = Grids.of(User.class)
         *     .column(User::getUsername)
         *         .text(Texts.label("user.username"))
         *         .and()
         *     .column(user -> user.getAddress().getCity())
         *         .text(Texts.label("user.city"))
         *         .and()
         *     .build();
         * }</pre>
         *
         * @param valueProvider value provider for the column
         * @return {@code ColumnBuilder<T>} to provide further fluent operations on {@code Grid.Column<T>} level
         */
        public <V> ColumnBuilder<T, V> column(ValueProvider<T, V> valueProvider) {
            return new ColumnBuilder<>(this, grid.addColumn(valueProvider));
        }

        /**
         * Adds a column using a custom {@link Renderer}.
         *
         * <p>Example:</p>
         *
         * <pre>{@code
         * Grid<User> grid = Grids.of(User.class)
         *     .column(new ComponentRenderer<>(user -> {
         *           Span badge = new Span(user.getStatus());
         *           badge.getElement().getThemeList().add(
         *              user.isActive() ? "badge success" : "badge error"
         *           );
         *            return badge;
         *        }))
         *         .text(Texts.label("user.status"))
         *         .and()
         *     .build();
         * }</pre>
         *
         * @param columnRenderer renderer used to display the column content
         * @return {@code ColumnBuilder<T>} to provide further fluent operations on {@code Grid.Column<T>} level
         */
        public <V> ColumnBuilder<T, V> column(Renderer<T> columnRenderer) {
            return new ColumnBuilder<>(this, grid.addColumn(columnRenderer));
        }

        /**
         * creates a column containing a Radiobutton to reflect the selection.
         *
         * <p>Example:</p>
         *
         * <pre>{@code
         * Grid<SalesForceContactDTO> grid = Grids.of(SalesForceContactDTO.class)
         *        .textEmptyState(Texts.description("grid.description"))
         *        .column(SalesForceContactDTO::getDisplayName).text(Texts.label("salesforce.displayName")).width("400px").and()
         *        .column(SalesForceContactDTO::getCustomerNumber).text(Texts.label("salesforce.customerNumber")).and()
         *        .selectionColumnRadio().and()
         *        .build();
         * }</pre>
         * <p>
         * <p>
         * might be changed to something like selectionColumn(SelectionStyle.RADIO_INDICATOR) if we need others
         *
         * @return {@code ColumnBuilder<T>} to provide further fluent operations on {@code Grid.Column<T>} level
         */
        public <V> ColumnBuilder<T, V> selectionColumnRadio() {
            AtomicReference<T> selected = new AtomicReference<>();

            grid.addSelectionListener(e -> {
                e.getFirstSelectedItem().ifPresent(selected::set);
                grid.getDataProvider().refreshAll();
            });

            Grid.Column<T> column = grid.addColumn(new ComponentRenderer<>(item -> {
                UnGroupedRadioButton radio = new UnGroupedRadioButton();

                radio.setChecked(item.equals(selected.get()));
                radio.addValueChangeListener((event) -> {
                    selected.set(item);
                    grid.select(item);
                });
                return radio;
            }));
            return new ColumnBuilder<>(this, column);
        }

        /**
         * creates a column containing a {@link NativeLabel} used to display 1 char.
         * For convenience: we accept a string and only use the first char.
         *
         * <p>Example:</p>
         *
         * <pre>{@code
         * Grid<SalesForceContactDTO> grid = Grids.of(SalesForceContactDTO.class)
         *        .textEmptyState(Texts.description("grid.description"))
         *        .column(SalesForceContactDTO::getDisplayName).text(Texts.label("salesforce.displayName")).width("400px").and()
         *        .column(SalesForceContactDTO::getCustomerNumber).text(Texts.label("salesforce.customerNumber")).and()
         *        .selectionColumnRadio().and()
         *        .build();
         * }</pre>
         * <p>
         * <p>
         * might be changed to something like selectionColumn(SelectionStyle.RADIO_INDICATOR) if we need others
         *
         * @return {@code ColumnBuilder<T>} to provide further fluent operations on {@code Grid.Column<T>} level
         */
        public ColumnBuilder<T, ?> columnIndexDot(Function<T, String> extractor) {
            Grid.Column<T> column = grid.addColumn(new ComponentRenderer<>(item -> {
                String value = extractor.apply(item);
                String letter = (value != null && !value.isBlank()) ? value.substring(0, 1).toUpperCase() : "";

                return new NativeLabel(letter);
            }));
            return new ColumnBuilder<>(this, column);
        }

        /**
         * adds CSS class to the {@link Grid}
         *
         * @param className CSS class
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         * @see Grid#addClassName(String)
         */
        public GridBuilder<T> withClassName(String className) {
            grid.addClassName(className);
            return this;
        }

        /**
         * adds Theme name(s) to the {@link Grid}
         *
         * @param themeNames - CSS theme(s)
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         * @see Grid#addThemeName(String)
         */
        public GridBuilder<T> withTheme(String... themeNames) {
            for (String theme : themeNames) {
                grid.addThemeName(theme);
            }
            return this;
        }

        /**
         * Enables i18n resolution for column texts.
         */
        public GridBuilder<T> withI18n(I18n i18n) {
            this.i18n = i18n;
            return this;
        }
        /**
         * Sets the width of the component to "100%".
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> fullWidth() {
            grid.setWidthFull();
            return this;
        }

        /**
         * Sets the width of the component.
         * The width should be in a format understood by the browser, e.g. "100px" or "2.5em".
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> width(String width) {
            grid.setWidth(width);
            return this;
        }

        /**
         * Sets the height of the grid
         * The height should be in a format understood by the browser, e.g. "100px" or "2.5em".
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> height(String heightStr) {
            grid.setHeight(heightStr);
            return this;
        }

        /**
         * empty state component which will be displayed when grid is loaded first without items.
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> emptyState(Component content) {
            grid.setEmptyStateComponent(content);
            return this;
        }

        /**
         * empty text (wrapped in a <code>&lt;span&gt;</code>) which will be displayed when grid is loaded first without items.
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> textEmptyState(Text text) {
            return emptyState(new Span(Texts.resolve(i18n, text)));
        }

        /**
         *
         * <pre>{@code
         * Grid<SalesForceContactDTO> sfGrid = Grids.of(SalesForceContactDTO.class)
         *                 .column(SalesForceContactDTO::getCustomerNumber).text(Texts.label("salesforce.number")).and()
         *                 .selectable( selectedContact -> { doSomething(selectedContact); } )
         *                 .build();
         * }</pre>
         *
         * @param onSelect {@code Consumer<T>} for the selection
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> selectable(Consumer<T> onSelect) {
            grid.setSelectionMode(Grid.SelectionMode.SINGLE);
            grid.addSelectionListener(e -> e.getFirstSelectedItem().ifPresent(onSelect));
            return this;
        }

        /**
         * <b>DIRECT ACCESS - USE SPARINGLY</b>
         *
         * <pre>{@code
         * Grid<SalesForceContactDTO> sfGrid = Grids.of(SalesForceContactDTO.class)
         *    .column(SalesForceContactDTO::getCustomerNumber).text(Texts.label("salesforce.number")).and()
         *    .selectable(selectedContact-> { doSomething(selectedContact);})
         *     .configure(grid ->{
         *             grid.setWidth("400px");
         *             grid.setItems(items);
         *             })
         *      .build();
         * }</pre>
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> configure(Consumer<Grid<T>> gridConfig) {
            gridConfig.accept(this.grid);
            return this;
        }

    }

    public static final class AutoGridBuilder<T> {
        private final GridBuilder<T> delegate;
        private final UiMeta<T> uiMeta;
        private final Set<String> excludedProperties = new LinkedHashSet<>();
        private final Map<String, Consumer<Grid.Column<T>>> overrides = new LinkedHashMap<>();

        private AutoGridBuilder(Class<T> type) {
            Objects.requireNonNull(type, "type");
            this.delegate = Grids.of(type);
            this.uiMeta = UiInspector.inspect(type);
        }

        public AutoGridBuilder<T> items(Collection<T> items) {
            delegate.items(items);
            return this;
        }

        public AutoGridBuilder<T> items(Supplier<? extends Collection<T>> getter) {
            delegate.items(getter);
            return this;
        }

        public AutoGridBuilder<T> items(DataProvider<T, ?> provider) {
            delegate.items(provider);
            return this;
        }

        public AutoGridBuilder<T> withClassName(String className) {
            delegate.withClassName(className);
            return this;
        }

        public AutoGridBuilder<T> withTheme(String... themeNames) {
            delegate.withTheme(themeNames);
            return this;
        }

        public AutoGridBuilder<T> withI18n(I18n i18n) {
            delegate.withI18n(i18n);
            return this;
        }

        public AutoGridBuilder<T> fullWidth() {
            delegate.fullWidth();
            return this;
        }

        public AutoGridBuilder<T> width(String width) {
            delegate.width(width);
            return this;
        }

        public AutoGridBuilder<T> height(String heightStr) {
            delegate.height(heightStr);
            return this;
        }

        public AutoGridBuilder<T> emptyState(Component content) {
            delegate.emptyState(content);
            return this;
        }

        public AutoGridBuilder<T> textEmptyState(Text text) {
            delegate.textEmptyState(text);
            return this;
        }

        public AutoGridBuilder<T> selectable(Consumer<T> onSelect) {
            delegate.selectable(onSelect);
            return this;
        }

        public AutoGridBuilder<T> configure(Consumer<Grid<T>> gridConfig) {
            delegate.configure(gridConfig);
            return this;
        }

        public AutoGridBuilder<T> exclude(String... propertyNames) {
            if (propertyNames == null) {
                return this;
            }
            Arrays.stream(propertyNames)
                    .filter(Objects::nonNull)
                    .forEach(excludedProperties::add);
            return this;
        }

        public AutoGridBuilder<T> override(String propertyName, Consumer<Grid.Column<T>> customizer) {
            Objects.requireNonNull(propertyName, "propertyName");
            Objects.requireNonNull(customizer, "customizer");
            overrides.merge(propertyName, customizer, Consumer::andThen);
            return this;
        }

        public Grid<T> build() {
            uiMeta.properties()
                    .filter(UiProperty::isVisible)
                    .filter(property -> !excludedProperties.contains(property.name()))
                    .forEach(this::addColumn);
            return delegate.build();
        }

        private void addColumn(UiProperty<T> property) {
            ColumnBuilder<T, Object> columnBuilder = delegate.column(item -> property.read(item));
            columnBuilder.configure(column -> {
                column.setKey(property.name());
                column.setHeader(property.label());
            });
            Consumer<Grid.Column<T>> override = overrides.get(property.name());
            if (override != null) {
                columnBuilder.configure(override);
            }
        }
    }

    /**
     * TODO: aaron: doku
     */
    public static class ColumnBuilder<T, V> {
        private final GridBuilder<T> tGridBuilder;
        private final Grid.Column<T> tColumn;

        private ColumnBuilder(GridBuilder<T> tGridBuilder, Grid.Column<T> tColumn) {
            this.tGridBuilder = tGridBuilder;
            this.tColumn = tColumn;
        }

        /**
         * Sets a header component to the column.
         *
         * @param headerComponent component to use as header
         * @return {@code ColumnBuilder<T>} to provide further fluent operations on {@code Grid.Column<T>} level
         */
        public ColumnBuilder<T, V> header(Component headerComponent) {
            tColumn.setHeader(headerComponent);
            return this;
        }

        /**
         * Sets the header text using the Text model.
         *
         * <p>Only LABEL is used; TOOLTIP is ignored.</p>
         */
        public ColumnBuilder<T, V> text(Text text) {
            if (text == null) {
                return this;
            }
            if (text.role() == TextRole.LABEL) {
                tColumn.setHeader(Texts.resolve(tGridBuilder.i18n, text));
            }
            return this;
        }
        /**
         * Sets the width of this column as a CSS-string.
         *
         * @param cssWidth as understood by browser
         * @return {@code ColumnBuilder<T>} to provide further fluent operations on {@code Grid.Column<T>} level
         */
        public ColumnBuilder<T, V> width(String cssWidth) {
            tColumn.setWidth(cssWidth);
            return this;
        }

        /**
         * Set the renderer for this column.
         *
         * @param renderer column renderer
         * @return {@code ColumnBuilder<T>} to provide further fluent operations on {@code Grid.Column<T>} level
         */
        public ColumnBuilder<T, V> withRenderer(Renderer<T> renderer) {
            tColumn.setRenderer(renderer);
            return this;
        }


        /**
         * <b>DIRECT ACCESS - USE SPARINGLY</b>
         *
         * <pre>{@code
         * Grid<SalesForceContactDTO> sfGrid = Grids.of(SalesForceContactDTO.class)
         *      .column(SalesForceContactDTO::getCustomerNumber).text(Texts.label("salesforce.number"))
         *      .configure(column-> column.setFlexGrow(0)).and()
         *      .build();
         * }</pre>
         *
         * @return {@code ColumnBuilder<T>} to provide further fluent operations on {@code Grid.Column<T>} level
         */
        public ColumnBuilder<T, V> configure(Consumer<Grid.Column<T>> column) {
            column.accept(tColumn);
            return this;
        }

        /**
         * returning containing parent builder to keep "sentence" going:
         *
         *<pre>{@code
         * Grid<ApprovalDetailsForSalesForceDTO> grid = Grids.of(ApprovalDetailsForSalesForceDTO.class)
         *                 .columnIndexDot(r -> r.salesForceContactDTO().getDisplayName()).and()
         *                 .column(r -> r.salesForceContactDTO().getDisplayName()).text(Texts.label("salesforce.displayName")).and()
         *                 .column(r -> r.item().getCustomerNumber()).text(Texts.label("salesforce.customerNumber")).and()
         *                 .column(this::renderValidity).text(Texts.label("salesforce.validity")).and()
         *                 .column(this::renderStatus).text(Texts.label("salesforce.status")).and()
         *                 .build();}</pre>
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> and() {
            return this.tGridBuilder;
        }
    }

}
// TODO: Grids.column.button
//  //
//  (vgl. CrudView: grid.addColumn(new ComponentRenderer<>(item -> {
//                    Button edit = Buttons.create()
//                            .text(Texts.label("Edit"))
//                            .style(ButtonVariant.LUMO_TERTIARY_INLINE)
//                            .action(() -> onEdit(item))
//                            .build();
//                    Button delete = Buttons.create()
//                            .text(Texts.label("Delete"))
//                            .style(ButtonVariant.LUMO_ERROR)
//                            .style(ButtonVariant.LUMO_TERTIARY_INLINE)
//                            .action(() -> onDelete(item))
//                            .build();
//                    return Layouts.hbox(edit, delete);
//                }))
