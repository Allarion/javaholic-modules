package de.javaholic.toolkit.ui.crud;

import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.Grids;
import de.javaholic.toolkit.ui.form.Forms;
import de.javaholic.toolkit.ui.meta.UiInspector;
import de.javaholic.toolkit.ui.meta.UiProperty;
import de.javaholic.toolkit.ui.text.DefaultTextResolver;
import de.javaholic.toolkit.ui.text.TextResolver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Factory for fluent CRUD panel builders.
 *
 * <p>Example manual mode:</p>
 * <pre>{@code
 * CrudPanel<User> panel = CrudPanels.of(User.class)
 *         .withStore(store)
 *         .withGrid(Grids.of(User.class).build())
 *         .withForm(Forms.of(User.class).build())
 *         .build();
 * }</pre>
 *
 * <p>Example auto mode:</p>
 * <pre>{@code
 * CrudPanel<User> panel = CrudPanels.auto(User.class)
 *         .withStore(store)
 *         .withTextResolver(key -> key)
 *         .build();
 * }</pre>
 */
public final class CrudPanels {

    private CrudPanels() {
    }

    /**
     * Starts a manual CRUD builder.
     *
     * <p>Example: {@code CrudPanels.of(User.class).withStore(store).build();}</p>
     */
    public static <T> CrudBuilder<T> of(Class<T> type) {
        return new ManualBuilder<>(type);
    }

    /**
     * Starts an automatic CRUD builder backed by UiMeta.
     *
     * <p>Example: {@code CrudPanels.auto(User.class).withStore(store).build();}</p>
     */
    public static <T> AutoCrudBuilder<T> auto(Class<T> type) {
        return new AutoBuilder<>(type);
    }

    /**
     * Fluent manual CRUD builder.
     *
     * <p>Example: {@code CrudPanels.of(User.class).withStore(store).build();}</p>
     */
    public interface CrudBuilder<T> {

        /**
         * Sets the backing store.
         *
         * <p>Example: {@code builder.withStore(store);}</p>
         */
        CrudBuilder<T> withStore(CrudStore<T, ?> store);

        /**
         * Injects a prebuilt grid.
         *
         * <p>Example: {@code builder.withGrid(Grids.of(User.class).build());}</p>
         */
        CrudBuilder<T> withGrid(Grid<T> grid);

        /**
         * Injects a prebuilt form.
         *
         * <p>Example: {@code builder.withForm(Forms.of(User.class).build());}</p>
         */
        CrudBuilder<T> withForm(Forms.Form<T> form);

        /**
         * Sets text resolution for default manual components.
         *
         * <p>Example: {@code builder.withTextResolver(key -> key);}</p>
         */
        CrudBuilder<T> withTextResolver(TextResolver resolver);

        /**
         * Sets a property filter for default manual components.
         *
         * <p>Example: {@code builder.withPropertyFilter(UiProperty::isVisible);}</p>
         */
        CrudBuilder<T> withPropertyFilter(Predicate<UiProperty<T>> filter);

        /**
         * Builds the CRUD panel.
         *
         * <p>Example: {@code CrudPanel<User> panel = builder.build();}</p>
         */
        CrudPanel<T> build();
    }

    /**
     * Fluent auto CRUD builder.
     *
     * <p>Example: {@code CrudPanels.auto(User.class).withStore(store).build();}</p>
     */
    public interface AutoCrudBuilder<T> {

        /**
         * Sets the backing store.
         *
         * <p>Example: {@code builder.withStore(store);}</p>
         */
        AutoCrudBuilder<T> withStore(CrudStore<T, ?> store);

        /**
         * Sets text resolution for auto-generated labels.
         *
         * <p>Example: {@code builder.withTextResolver(key -> messages.get(key));}</p>
         */
        AutoCrudBuilder<T> withTextResolver(TextResolver resolver);

        /**
         * Sets a property filter applied to auto-generated grid and form fields.
         *
         * <p>Example: {@code builder.withPropertyFilter(UiProperty::isVisible);}</p>
         */
        AutoCrudBuilder<T> withPropertyFilter(Predicate<UiProperty<T>> filter);

        /**
         * Overrides one auto-generated property for both grid and form contexts.
         *
         * <p>Example: {@code builder.override("email", cfg -> cfg.label("user.email"));}</p>
         */
        AutoCrudBuilder<T> override(String propertyName, Consumer<PropertyConfig<T>> config);

        /**
         * Builds the CRUD panel.
         *
         * <p>Example: {@code CrudPanel<User> panel = builder.build();}</p>
         */
        CrudPanel<T> build();
    }

    /**
     * Shared semantic override definition for auto properties.
     *
     * <p>Example: {@code cfg.label("user.email");}</p>
     */
    public static final class PropertyConfig<T> {
        private String labelKey;
        private Consumer<Grid.Column<T>> gridCustomizer;
        private Consumer<HasValue<?, ?>> formCustomizer;

        /**
         * Sets a semantic label key for both grid header and form field label.
         *
         * <p>Example: {@code cfg.label("user.email");}</p>
         */
        public PropertyConfig<T> label(String labelKey) {
            this.labelKey = Objects.requireNonNull(labelKey, "labelKey");
            return this;
        }

        /**
         * Adds grid-specific column customization.
         *
         * <p>Example: {@code cfg.grid(col -> col.setAutoWidth(true));}</p>
         */
        public PropertyConfig<T> grid(Consumer<Grid.Column<T>> customizer) {
            Objects.requireNonNull(customizer, "customizer");
            this.gridCustomizer = this.gridCustomizer == null ? customizer : this.gridCustomizer.andThen(customizer);
            return this;
        }

        /**
         * Adds form-specific field customization.
         *
         * <p>Example: {@code cfg.form(field -> field.setReadOnly(true));}</p>
         */
        public PropertyConfig<T> form(Consumer<HasValue<?, ?>> customizer) {
            Objects.requireNonNull(customizer, "customizer");
            this.formCustomizer = this.formCustomizer == null ? customizer : this.formCustomizer.andThen(customizer);
            return this;
        }
    }

    private static final class ManualBuilder<T> implements CrudBuilder<T> {
        private final Class<T> type;
        private CrudStore<T, ?> store;
        private Grid<T> grid;
        private Forms.Form<T> form;
        private TextResolver textResolver = new DefaultTextResolver();
        private Predicate<UiProperty<T>> propertyFilter = UiProperty::isVisible;

        private ManualBuilder(Class<T> type) {
            this.type = Objects.requireNonNull(type, "type");
        }

        @Override
        public CrudBuilder<T> withStore(CrudStore<T, ?> store) {
            this.store = Objects.requireNonNull(store, "store");
            return this;
        }

        @Override
        public CrudBuilder<T> withGrid(Grid<T> grid) {
            this.grid = Objects.requireNonNull(grid, "grid");
            return this;
        }

        @Override
        public CrudBuilder<T> withForm(Forms.Form<T> form) {
            this.form = Objects.requireNonNull(form, "form");
            return this;
        }

        @Override
        public CrudBuilder<T> withTextResolver(TextResolver resolver) {
            this.textResolver = Objects.requireNonNull(resolver, "resolver");
            return this;
        }

        @Override
        public CrudBuilder<T> withPropertyFilter(Predicate<UiProperty<T>> filter) {
            this.propertyFilter = Objects.requireNonNull(filter, "filter");
            return this;
        }

        @Override
        public CrudPanel<T> build() {
            Objects.requireNonNull(store, "store");
            String[] excluded = excludedPropertyNames(type, propertyFilter);
            Grid<T> effectiveGrid = grid != null
                    ? grid
                    : Grids.of(type).withTextResolver(textResolver).build();
            return new CrudPanel<>(type, store, effectiveGrid, () -> form != null
                    ? form
                    : Forms.auto(type)
                    .withTextResolver(textResolver)
                    .exclude(excluded)
                    .build());
        }
    }

    private static final class AutoBuilder<T> implements AutoCrudBuilder<T> {
        private final Class<T> type;
        private CrudStore<T, ?> store;
        private TextResolver textResolver = new DefaultTextResolver();
        private Predicate<UiProperty<T>> propertyFilter = UiProperty::isVisible;
        private final Map<String, PropertyConfig<T>> overrides = new LinkedHashMap<>();

        private AutoBuilder(Class<T> type) {
            this.type = Objects.requireNonNull(type, "type");
        }

        @Override
        public AutoCrudBuilder<T> withStore(CrudStore<T, ?> store) {
            this.store = Objects.requireNonNull(store, "store");
            return this;
        }

        @Override
        public AutoCrudBuilder<T> withTextResolver(TextResolver resolver) {
            this.textResolver = Objects.requireNonNull(resolver, "resolver");
            return this;
        }

        @Override
        public AutoCrudBuilder<T> withPropertyFilter(Predicate<UiProperty<T>> filter) {
            this.propertyFilter = Objects.requireNonNull(filter, "filter");
            return this;
        }

        @Override
        public AutoCrudBuilder<T> override(String propertyName, Consumer<PropertyConfig<T>> config) {
            Objects.requireNonNull(propertyName, "propertyName");
            Objects.requireNonNull(config, "config");
            PropertyConfig<T> propertyConfig = overrides.computeIfAbsent(propertyName, key -> new PropertyConfig<>());
            config.accept(propertyConfig);
            return this;
        }

        @Override
        public CrudPanel<T> build() {
            Objects.requireNonNull(store, "store");
            String[] excluded = excludedPropertyNames(type, propertyFilter);
            Grid<T> grid = buildGrid(excluded);
            return new CrudPanel<>(type, store, grid, () -> buildForm(excluded));
        }

        private Grid<T> buildGrid(String[] excluded) {
            Grids.AutoGridBuilder<T> builder = Grids.auto(type)
                    .withTextResolver(textResolver)
                    .exclude(excluded);
            overrides.forEach((propertyName, config) -> builder.override(propertyName, column -> {
                if (config.labelKey != null) {
                    column.setHeader(resolve(config.labelKey));
                }
                if (config.gridCustomizer != null) {
                    config.gridCustomizer.accept(column);
                }
            }));
            return builder.build();
        }

        private Forms.Form<T> buildForm(String[] excluded) {
            Forms.AutoFormBuilder<T> builder = Forms.auto(type)
                    .withTextResolver(textResolver)
                    .exclude(excluded);
            overrides.forEach((propertyName, config) -> builder.override(propertyName, field -> {
                if (config.labelKey != null && field instanceof HasLabel hasLabel) {
                    hasLabel.setLabel(resolve(config.labelKey));
                }
                if (config.formCustomizer != null) {
                    config.formCustomizer.accept(field);
                }
            }));
            return builder.build();
        }

        private String resolve(String key) {
            String resolved = textResolver.resolve(key);
            return resolved != null ? resolved : key;
        }
    }

    private static <T> String[] excludedPropertyNames(Class<T> type, Predicate<UiProperty<T>> propertyFilter) {
        return UiInspector.inspect(type).properties()
                .filter(property -> !propertyFilter.test(property))
                .map(UiProperty::name)
                .toArray(String[]::new);
    }
}
