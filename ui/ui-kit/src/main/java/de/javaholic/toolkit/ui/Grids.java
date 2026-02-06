package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Fluent builder for {@link Grid} configuration.<br>
 *
 * <p>Example:</p>
 *
 * <pre>{@code
 * Grids.of(User.class)
 *     .items(users)
 *     .column(User::getUsername)
 *         .header("Username")
 *         .and()
 *     .column(new ComponentRenderer<>(user -> new Icon("vaadin", "user")))
 *         .header("Icon")
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

    public static class GridBuilder<T> {

        private final Grid<T> grid;

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
         *          .column(User::getId).header("ID").and()
         *          .column(User::getName).header("Name").and()
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
         *         .header("Username")
         *         .and()
         *     .column(user -> user.getAddress().getCity())
         *         .header("City")
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
         *         .header("Status")
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
         *        .emptyStateHtml(i18n.getMessage("grid.description"))
         *        .column(SalesForceContactDTO::getDisplayName).header("Kundenname").width("400px").and()
         *        .column(SalesForceContactDTO::getCustomerNumber).header("Kundennummer").and()
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
         * For convenience we accept a string and only use the first char.
         *
         * <p>Example:</p>
         *
         * <pre>{@code
         * Grid<SalesForceContactDTO> grid = Grids.of(SalesForceContactDTO.class)
         *        .emptyStateHtml(i18n.getMessage("grid.description"))
         *        .column(SalesForceContactDTO::getDisplayName).header("Kundenname").width("400px").and()
         *        .column(SalesForceContactDTO::getCustomerNumber).header("Kundennummer").and()
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
         * @param className css class
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
         * @param themeNames - css theme(s)
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
        public GridBuilder<T> emptyStateText(String text) {
            return emptyState(new Span(text));
        }

        /**
         * empty state HTML content (wrapped unescaped in a <code>&lt;div&gt;</code>) which will be displayed when grid is loaded first without items.
         * <p>
         * Use sparingly.
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> emptyStateHtml(String html) {
            Html htmlComponent = new Html("<div>" + html + "</div>");
            return emptyState(htmlComponent);
        }

        /**
         *
         * <pre>{@code
         * Grid<SalesForceContactDTO> sfGrid = Grids.of(SalesForceContactDTO.class)
         *                 .column(SalesForceContactDTO::getCustomerNumber).header("Nr").and()
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
         *    .column(SalesForceContactDTO::getCustomerNumber).header("Nr").and()
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
         * Sets a header text to the column.
         *
         * @param labelText text to be shown at column header
         * @return {@code ColumnBuilder<T>} to provide further fluent operations on {@code Grid.Column<T>} level
         */
        public ColumnBuilder<T, V> header(String labelText) {
            tColumn.setHeader(labelText);
            return this;
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
         *      .column(SalesForceContactDTO::getCustomerNumber).header("Nr")
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
         * returing containing parent builder to keep "sentence" going:
         *
         *<pre>{@code
         * Grid<ApprovalDetailsForSalesForceDTO> grid = Grids.of(ApprovalDetailsForSalesForceDTO.class)
         *                 .columnIndexDot(r -> r.salesForceContactDTO().getDisplayName()).and()
         *                 .column(r -> r.salesForceContactDTO().getDisplayName()).header("Kundenname").and()
         *                 .column(r -> r.item().getCustomerNumber()).header("Kundennummer").and()
         *                 .column(this::renderValidity).header("Gültigkeit").and()
         *                 .column(this::renderStatus).header("Status").and()
         *                 .build();}</pre>
         *
         * @return {@code GridBuilder<T>} to provide further fluent operations on {@code Grid} level
         */
        public GridBuilder<T> and() {
            return this.tGridBuilder;
        }
    }


    public static class UnGroupedRadioButton extends RadioButtonGroup<String> {

        public UnGroupedRadioButton() {
            this.setItems(List.of(""));
        }

        public void setChecked(final boolean checked) {
            super.setValue(checked ? "" : null);
        }
    }
}