package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.ui.component.UnGroupedRadioButton;
import de.javaholic.toolkit.ui.meta.UiInspector;
import de.javaholic.toolkit.ui.meta.UiMeta;
import de.javaholic.toolkit.ui.meta.UiProperty;
import de.javaholic.toolkit.i18n.DefaultTextResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Grid builder facade with manual and convention-based auto modes.
 *
 * <p>Responsibility:</p>
 * <ul>
 * <li>Provide fluent APIs for manual column definition ({@link #of(Class)})</li>
 * <li>Provide convention-based column generation from {@link UiMeta} ({@link #auto(Class)})</li>
 * </ul>
 *
 * <p>Must not do: call {@code BeanIntrospector}/{@code BeanMeta} directly in Grid assembly.
 * UI metadata must come from {@link UiInspector}/{@link UiMeta} to keep layering intact.</p>
 *
 * <p>Architecture fit: rendering layer. It consumes UI semantics and renders Vaadin {@link Grid}
 * components; it does not define metadata policy.</p>
 *
 * <p>Manual example:</p>
 * <pre>{@code
 * Grid<User> manual = Grids.of(User.class)
 *     .column(User::getUsername).header("user.username").and()
 *     .column(User::getEmail).header("user.email").and()
 *     .build();
 * }</pre>
 *
 * <p>Auto example:</p>
 * <pre>{@code
 * Grid<User> auto = Grids.auto(User.class).build();
 * }</pre>
 *
 * <p>Auto with overrides:</p>
 * <pre>{@code
 * Grid<User> customized = Grids.auto(User.class)
 *     .exclude("password")
 *     .override("email", col -> col.setAutoWidth(true))
 *     .build();
 * }</pre>
 *
 * <p>Auto with annotation + text resolver:</p>
 * <pre>{@code
 * Grid<User> grid = Grids.auto(User.class)
 *     .withTextResolver(key -> messages.getOrDefault(key, key))
 *     .build();
 * }</pre>
 *
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
     *
     * <p>Supports semantic annotations like {@code @UiLabel}, {@code @UiOrder}, and
     * {@code @UiHidden} through UiMeta.</p>
     *
     * <p>Example: {@code Grid<User> grid = Grids.auto(User.class).build();}</p>
     */
    public static <T> AutoGridBuilder<T> auto(Class<T> type) {
        return new AutoGridBuilder<>(type);
    }

    /**
     * Manual grid builder for explicit column declarations.
     *
     * <p>Example:</p>
     * <pre>{@code
     * Grid<User> grid = Grids.of(User.class)
     *     .column(User::getUsername).and()
     *     .build();
     * }</pre>
     */
    public static class GridBuilder<T> {

        private final Grid<T> grid;
        // GridBuilder keeps semantic keys and resolves only when applying column headers/empty state.
        private TextResolver textResolver = new DefaultTextResolver();

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
         *          .column(User::getId).header("user.id").and()
         *          .column(User::getName).header("user.name").and()
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
         *         .header("user.username")
         *         .and()
         *     .column(user -> user.getAddress().getCity())
         *         .header("user.city")
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
         *         .header("user.status")
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
         *        .textEmptyState("grid.description")
         *        .column(SalesForceContactDTO::getDisplayName).header("salesforce.displayName").width("400px").and()
         *        .column(SalesForceContactDTO::getCustomerNumber).header("salesforce.customerNumber").and()
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
         *        .textEmptyState("grid.description")
         *        .column(SalesForceContactDTO::getDisplayName).header("salesforce.displayName").width("400px").and()
         *        .column(SalesForceContactDTO::getCustomerNumber).header("salesforce.customerNumber").and()
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
         * Sets a key resolver for grid text.
         *
         * <p>Example: {@code Grids.of(User.class).withTextResolver(key -> key).build();}</p>
         */
        public GridBuilder<T> withTextResolver(TextResolver textResolver) {
            this.textResolver = Objects.requireNonNull(textResolver, "textResolver");
            return this;
        }
        /**
         * Sets the width of the component to "100%".
         *
         * <p>Example: {@code Grids.of(User.class).fullWidth().build();}</p>
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
         * <p>Example: {@code Grids.of(User.class).width("640px").build();}</p>
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
         * <p>Example: {@code Grids.of(User.class).height("420px").build();}</p>
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
         * <p>Example: {@code Grids.of(User.class).emptyState(new Span("No users")).build();}</p>
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
         * <p>Example: {@code Grids.of(User.class).textEmptyState("users.empty").build();}</p>
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> textEmptyState(String key) {
            return emptyState(new Span(resolve(key)));
        }

        private String resolve(String key) {
            return textResolver.resolve(key).orElse(key);
        }

        /**
         *
         * <pre>{@code
         * Grid<SalesForceContactDTO> sfGrid = Grids.of(SalesForceContactDTO.class)
         *                 .column(SalesForceContactDTO::getCustomerNumber).header("salesforce.number").and()
         *                 .selectable( selectedContact -> { doSomething(selectedContact); } )
         *                 .build();
         * }</pre>
         *
         * @param onSelect {@code Consumer<T>} for the selection
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> selectable(Consumer<T> onSelect) {
            grid.setSelectionMode(Grid.SelectionMode.SINGLE); // TODO: Whats about Grid.SelectionMode.MULTI? add something like .multiselect(able)
            grid.addSelectionListener(e -> e.getFirstSelectedItem().ifPresent(onSelect));
            return this;
        }

        /**
         * <b>DIRECT ACCESS - USE SPARINGLY</b>
         *
         * <pre>{@code
         * Grid<SalesForceContactDTO> sfGrid = Grids.of(SalesForceContactDTO.class)
         *    .column(SalesForceContactDTO::getCustomerNumber).header("salesforce.number").and()
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

    /**
     * Convention-based grid builder that consumes {@link UiMeta}.
     *
     * <p>Example:</p>
     * <pre>{@code
     * Grid<User> grid = Grids.auto(User.class)
     *     .exclude("password")
     *     .build();
     * }</pre>
     */
    public static final class AutoGridBuilder<T> {
        private final GridBuilder<T> delegate;
        private final UiMeta<T> uiMeta;
        private final Set<String> excludedProperties = new LinkedHashSet<>();
        private final Map<String, Consumer<Grid.Column<T>>> overrides = new LinkedHashMap<>();
        private TextResolver textResolver = new DefaultTextResolver();

        private AutoGridBuilder(Class<T> type) {
            Objects.requireNonNull(type, "type");
            this.delegate = Grids.of(type);
            // ------------------------------------------------------------------
            // UI semantic annotation evaluation happens *only* in UiMeta.
            // Label resolution to actual display text happens *only* via TextResolver.
            // FieldRegistry consumes resolved attributes, but never evaluates annotations.
            // ------------------------------------------------------------------
            this.uiMeta = UiInspector.inspect(type);
        }

        /**
         * Sets in-memory items for the auto grid.
         *
         * <p>Example: {@code Grids.auto(User.class).items(users).build();}</p>
         */
        public AutoGridBuilder<T> items(Collection<T> items) {
            delegate.items(items);
            return this;
        }

        /**
         * Sets items using a supplier callback.
         *
         * <p>Example: {@code Grids.auto(User.class).items(service::findAll).build();}</p>
         */
        public AutoGridBuilder<T> items(Supplier<? extends Collection<T>> getter) {
            delegate.items(getter);
            return this;
        }

        /**
         * Sets a Vaadin data provider.
         *
         * <p>Example: {@code Grids.auto(User.class).items(provider).build();}</p>
         */
        public AutoGridBuilder<T> items(DataProvider<T, ?> provider) {
            delegate.items(provider);
            return this;
        }

        /**
         * Adds a CSS class to the grid.
         *
         * <p>Example: {@code Grids.auto(User.class).withClassName("users-grid").build();}</p>
         */
        public AutoGridBuilder<T> withClassName(String className) {
            delegate.withClassName(className);
            return this;
        }

        /**
         * Adds one or more Vaadin theme names.
         *
         * <p>Example: {@code Grids.auto(User.class).withTheme("compact").build();}</p>
         */
        public AutoGridBuilder<T> withTheme(String... themeNames) {
            delegate.withTheme(themeNames);
            return this;
        }

        /**
         * Sets a custom resolver for semantic text keys.
         *
         * <p>Example:</p>
         * <pre>{@code
         * Grids.auto(User.class)
         *     .withTextResolver(key -> bundle.getString(key))
         *     .build();
         * }</pre>
         */
        public AutoGridBuilder<T> withTextResolver(TextResolver textResolver) {
            TextResolver nonNullResolver = Objects.requireNonNull(textResolver, "textResolver");
            this.textResolver = nonNullResolver;
            this.delegate.withTextResolver(nonNullResolver);
            return this;
        }

        /**
         * Sets the grid width to 100%.
         *
         * <p>Example: {@code Grids.auto(User.class).fullWidth().build();}</p>
         */
        public AutoGridBuilder<T> fullWidth() {
            delegate.fullWidth();
            return this;
        }

        /**
         * Sets a custom CSS width.
         *
         * <p>Example: {@code Grids.auto(User.class).width("600px").build();}</p>
         */
        public AutoGridBuilder<T> width(String width) {
            delegate.width(width);
            return this;
        }

        /**
         * Sets a custom CSS height.
         *
         * <p>Example: {@code Grids.auto(User.class).height("420px").build();}</p>
         */
        public AutoGridBuilder<T> height(String heightStr) {
            delegate.height(heightStr);
            return this;
        }

        /**
         * Sets a custom empty state component.
         *
         * <p>Example: {@code Grids.auto(User.class).emptyState(new Span("No users")).build();}</p>
         */
        public AutoGridBuilder<T> emptyState(Component content) {
            delegate.emptyState(content);
            return this;
        }

        /**
         * Sets an empty state text using the text model.
         *
         * <p>Example: {@code Grids.auto(User.class).textEmptyState("users.empty").build();}</p>
         */
        public AutoGridBuilder<T> textEmptyState(String key) {
            delegate.textEmptyState(key);
            return this;
        }

        /**
         * Enables single-select behavior with callback.
         *
         * <p>Example: {@code Grids.auto(User.class).selectable(this::showDetails).build();}</p>
         */
        public AutoGridBuilder<T> selectable(Consumer<T> onSelect) {
            delegate.selectable(onSelect);
            return this;
        }

        /**
         * Applies direct access configuration to the underlying grid.
         *
         * <p>Example: {@code Grids.auto(User.class).configure(g -> g.setAllRowsVisible(true)).build();}</p>
         */
        public AutoGridBuilder<T> configure(Consumer<Grid<T>> gridConfig) {
            delegate.configure(gridConfig);
            return this;
        }

        /**
         * Excludes properties from auto-generated columns.
         *
         * <p>Example: {@code Grids.auto(User.class).exclude("password").build();}</p>
         */
        public AutoGridBuilder<T> exclude(String... propertyNames) {
            if (propertyNames == null) {
                return this;
            }
            Arrays.stream(propertyNames)
                    .filter(Objects::nonNull)
                    .forEach(excludedProperties::add);
            return this;
        }

        /**
         * Applies custom column configuration for one property key.
         *
         * <p>Example: {@code Grids.auto(User.class).override("email", col -> col.setAutoWidth(true)).build();}</p>
         */
        public AutoGridBuilder<T> override(String propertyName, Consumer<Grid.Column<T>> customizer) {
            Objects.requireNonNull(propertyName, "propertyName");
            Objects.requireNonNull(customizer, "customizer");
            overrides.merge(propertyName, customizer, Consumer::andThen);
            return this;
        }

        /**
         * Builds the configured auto grid.
         *
         * <p>Example: {@code Grid<User> grid = Grids.auto(User.class).build();}</p>
         */
        public Grid<T> build() {
            uiMeta.properties()
                    .filter(UiProperty::isVisible)
                    .filter(property -> !excludedProperties.contains(property.name()))
                    .sorted(Comparator.comparingInt(UiProperty::order))
                    .forEach(this::addColumn);
            return delegate.build();
        }

        private void addColumn(UiProperty<T> property) {
            ColumnBuilder<T, Object> columnBuilder = delegate.column(item -> property.read(item));
            columnBuilder.configure(column -> {
                column.setKey(property.name());
                column.setHeader(resolveLabel(property.labelKey()));
            });
            Consumer<Grid.Column<T>> override = overrides.get(property.name());
            if (override != null) {
                columnBuilder.configure(override);
            }
        }

        private String resolveLabel(String labelKey) {
            // TODO: revisit i18n key, see HierarchicalTextResolver for concept
            return textResolver.resolve(labelKey).orElse(labelKey);
        }
    }

    /**
     * Fluent column-level builder returned from {@link GridBuilder#column(ValueProvider)}.
     *
     * <p>Example:</p>
     * <pre>{@code
     * Grids.of(User.class)
     *     .column(User::getEmail).width("320px").and()
     *     .build();
     * }</pre>
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
         * Sets the header text key.
         *
         * <p>Example: {@code column.header("user.email");}</p>
         */
        public ColumnBuilder<T, V> header(String key) {
            if (key == null) {
                return this;
            }
            tColumn.setHeader(tGridBuilder.resolve(key));
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
         *      .column(SalesForceContactDTO::getCustomerNumber).header("salesforce.number")
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
         *                 .column(r -> r.salesForceContactDTO().getDisplayName()).header("salesforce.displayName").and()
         *                 .column(r -> r.item().getCustomerNumber()).header("salesforce.customerNumber").and()
         *                 .column(this::renderValidity).header("salesforce.validity").and()
         *                 .column(this::renderStatus).header("salesforce.status").and()
         *                 .build();}</pre>
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> and() {
            return this.tGridBuilder;
        }
    }

}
// TODO: Grids.column.button as shorthand for .column(return Buttons.create()); could also apply to to inputs. and maybe Forms...grid() as ref to Grids?


//  (vgl. CrudView: grid.addColumn(new ComponentRenderer<>(item -> {
//                    Button edit = Buttons.create()
//                            .label("Edit")
//                            .style(ButtonVariant.LUMO_TERTIARY_INLINE)
//                            .action(() -> onEdit(item))
//                            .build();
//                    Button delete = Buttons.create()
//                            .label("Delete")
//                            .style(ButtonVariant.LUMO_ERROR)
//                            .style(ButtonVariant.LUMO_TERTIARY_INLINE)
//                            .action(() -> onDelete(item))
//                            .build();
//                    return Layouts.hbox(edit, delete);
//                }))
// TODO (Architecture):
// Introduce internal ComponentFactory for shared component creation (buttons, fields, layouts). <- vgl Registry?
// Grid/Form should delegate internally to it, but must NOT expose Inputs builders directly.
// Goal: reuse + consistency without API-layer merging.


