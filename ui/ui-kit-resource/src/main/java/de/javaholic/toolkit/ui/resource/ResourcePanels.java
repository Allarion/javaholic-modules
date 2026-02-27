package de.javaholic.toolkit.ui.resource;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.grid.Grid;
import de.javaholic.toolkit.i18n.DefaultTextResolver;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.Grids;
import de.javaholic.toolkit.ui.api.ResourceAction;
import de.javaholic.toolkit.ui.form.Forms;
import de.javaholic.toolkit.ui.meta.UiInspector;
import de.javaholic.toolkit.ui.meta.UiMeta;
import de.javaholic.toolkit.ui.meta.UiProperty;
import de.javaholic.toolkit.ui.meta.UiPropertyConfig;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Factory for fluent Resource UI surface builders.
 *
 * <p>Internal term: DAF means "Dataset + Actions + Forms" (work title). The
 * ResourcePanels builders assemble that DAF UI surface in manual or auto mode.</p>
 *
 * <p>Concept: two entry points are provided.</p>
 * <ul>
 * <li>manual mode ({@link #of(Class)}) when callers provide explicit grid/form components</li>
 * <li>auto mode ({@link #auto(Class)}) when grid/form are generated from {@link UiMeta}</li>
 * </ul>
 *
 * <p>Both modes end in the same {@link GridFormsResourceView} orchestration component.</p>
 *
 * <p>Example manual mode:</p>
 * <pre>{@code
 * ResourcePanel<User> panel = ResourcePanels.of(User.class)
 *         .withStore(store)
 *         .withGrid(Grids.of(User.class).build())
 *         .withForm(Forms.of(User.class).build())
 *         .build();
 * }</pre>
 *
 * <p>Example auto mode:</p>
 * <pre>{@code
 * ResourcePanel<User> panel = ResourcePanels.auto(User.class)
 *         .withStore(store)
 *         .withTextResolver(key -> key)
 *         .build();
 * }</pre>
 */
public final class ResourcePanels {

    private ResourcePanels() {
    }

    /**
     * Starts a manual CRUD builder.
     *
     * <p>Example: {@code ResourcePanels.of(User.class).withStore(store).build();}</p>
     */
    public static <T> ManualCrudBuilder<T> of(Class<T> type) {
        return new ManualBuilder<>(type);
    }

    /**
     * Starts an automatic CRUD builder backed by UiMeta.
     *
     * <p>Example: {@code ResourcePanels.auto(User.class).withStore(store).build();}</p>
     */
    public static <T> AutoCrudBuilder<T> auto(Class<T> type) {
        return new AutoBuilder<>(type);
    }

    /**
     * Fluent manual CRUD builder.
     *
     * <p>Example: {@code ResourcePanels.of(User.class).withStore(store).build();}</p>
     */
    public interface CrudBuilder<T> {
        /**
         * Builds the CRUD panel.
         *
         * <p>Example: {@code ResourcePanel<User> panel = builder.build();}</p>
         */
        GridFormsResourceView<T> build();

        /**
         * Sets the backing store.
         *
         * <p>Example: {@code builder.withStore(store);}</p>
         */
        CrudBuilder<T> withStore(CrudStore<T, ?> store);

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

        CrudBuilder<T> withActionProvider(Class<?> actionProviderType);

        CrudBuilder<T> toolbarAction(ResourceAction.ToolbarAction<T> action);

        CrudBuilder<T> rowAction(ResourceAction.RowAction<T> action);

        CrudBuilder<T> selectionAction(ResourceAction.SelectionAction<T> action);
    }

    /**
     * Fluent manual CRUD builder.
     *
     * <p>Example: {@code ResourcePanels.of(User.class).withStore(store).build();}</p>
     */
    public interface ManualCrudBuilder<T> extends ResourcePanels.CrudBuilder<T> {
        /**
         * Sets the backing store.
         *
         * <p>Example: {@code builder.withStore(store);}</p>
         */
        ManualCrudBuilder<T> withStore(CrudStore<T, ?> store);

        /**
         * Sets text resolution for default manual components.
         *
         * <p>Example: {@code builder.withTextResolver(key -> key);}</p>
         */
        ManualCrudBuilder<T> withTextResolver(TextResolver resolver);

        /**
         * Sets a property filter for default manual components.
         *
         * <p>Example: {@code builder.withPropertyFilter(UiProperty::isVisible);}</p>
         */
        ManualCrudBuilder<T> withPropertyFilter(Predicate<UiProperty<T>> filter);

        ManualCrudBuilder<T> withActionProvider(Class<?> actionProviderType);

        ManualCrudBuilder<T> toolbarAction(ResourceAction.ToolbarAction<T> action);

        ManualCrudBuilder<T> rowAction(ResourceAction.RowAction<T> action);

        ManualCrudBuilder<T> selectionAction(ResourceAction.SelectionAction<T> action);

        /**
         * Injects a prebuilt grid.
         *
         * <p>Example: {@code builder.withGrid(Grids.of(User.class).build());}</p>
         */
        ManualCrudBuilder<T> withGrid(Grid<T> grid);

        /**
         * Injects a prebuilt form.
         *
         * <p>Example: {@code builder.withForm(Forms.of(User.class).build());}</p>
         */
        ManualCrudBuilder<T> withForm(Forms.Form<T> form);
    }

    /**
     * Fluent auto CRUD builder.
     *
     * <p>Example: {@code ResourcePanels.auto(User.class).withStore(store).build();}</p>
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

        AutoCrudBuilder<T> withActionProvider(Class<?> actionProviderType);

        // todo: cant this be 1 for all actions? the action know where it belongs
        AutoCrudBuilder<T> toolbarAction(ResourceAction.ToolbarAction<T> action);

        AutoCrudBuilder<T> rowAction(ResourceAction.RowAction<T> action);

        AutoCrudBuilder<T> selectionAction(ResourceAction.SelectionAction<T> action);

        /**
         * Overrides one auto-generated property before grid/form generation.
         *
         * <p>This method applies only to the auto builder variant and configures UI metadata
         * semantics before concrete components are built.</p>
         *
         * <p>Unknown property names cause {@link IllegalArgumentException} during {@link #build()}.</p>
         *
         * <p>Example: {@code builder.override("email", cfg -> cfg.label("user.email"));}</p>
         */
        AutoCrudBuilder<T> override(String propertyName, Consumer<UiPropertyConfig<T>> config);

        /**
         * Builds the CRUD panel.
         *
         * <p>Example: {@code ResourcePanel<User> panel = builder.build();}</p>
         */
        GridFormsResourceView<T> build();
    }

    private static final class ManualBuilder<T> implements ManualCrudBuilder<T> {
        private final Class<T> type;
        private CrudStore<T, ?> store;
        private Grid<T> grid;
        private Forms.Form<T> form;
        private TextResolver textResolver = new DefaultTextResolver();
        private Predicate<UiProperty<T>> propertyFilter = UiProperty::isVisible;
        private Class<?> actionProviderType;
        private final List<ResourceAction.ToolbarAction<T>> toolbarActions = new ArrayList<>();
        private final List<ResourceAction.RowAction<T>> rowActions = new ArrayList<>();
        private final List<ResourceAction.SelectionAction<T>> selectionActions = new ArrayList<>();

        private ManualBuilder(Class<T> type) {
            this.type = Objects.requireNonNull(type, "type");
        }

        @Override
        public ManualCrudBuilder<T> withStore(CrudStore<T, ?> store) {
            this.store = Objects.requireNonNull(store, "store");
            return this;
        }

        @Override
        public ManualCrudBuilder<T> withGrid(Grid<T> grid) {
            this.grid = Objects.requireNonNull(grid, "grid");
            return this;
        }

        @Override
        public ManualCrudBuilder<T> withForm(Forms.Form<T> form) {
            this.form = Objects.requireNonNull(form, "form");
            return this;
        }

        @Override
        public ManualCrudBuilder<T> withTextResolver(TextResolver resolver) {
            this.textResolver = Objects.requireNonNull(resolver, "resolver");
            return this;
        }

        @Override
        public ManualCrudBuilder<T> withPropertyFilter(Predicate<UiProperty<T>> filter) {
            this.propertyFilter = Objects.requireNonNull(filter, "filter");
            return this;
        }

        @Override
        public ManualCrudBuilder<T> withActionProvider(Class<?> actionProviderType) {
            this.actionProviderType = Objects.requireNonNull(actionProviderType, "actionProviderType");
            return this;
        }

        @Override
        public ManualCrudBuilder<T> toolbarAction(ResourceAction.ToolbarAction<T> action) {
            this.toolbarActions.add(Objects.requireNonNull(action, "action"));
            return this;
        }

        @Override
        public ManualCrudBuilder<T> rowAction(ResourceAction.RowAction<T> action) {
            this.rowActions.add(Objects.requireNonNull(action, "action"));
            return this;
        }

        @Override
        public ManualCrudBuilder<T> selectionAction(ResourceAction.SelectionAction<T> action) {
            this.selectionActions.add(Objects.requireNonNull(action, "action"));
            return this;
        }

        @Override
        public GridFormsResourceView<T> build() {
            Objects.requireNonNull(store, "store");
            String[] excluded = excludedPropertyNames(type, propertyFilter);
            Grid<T> effectiveGrid = grid != null
                    ? grid
                    : Grids.of(type).withTextResolver(textResolver).build();
            return new GridFormsResourceView<>(
                    type,
                    store,
                    effectiveGrid,
                    () -> form != null
                            ? form
                            : Forms.auto(type)
                            .withTextResolver(textResolver)
                            .exclude(excluded)
                            .build(),
                    actionProviderType,
                    toolbarActions,
                    rowActions,
                    selectionActions
            );
        }
    }

    private static final class AutoBuilder<T> implements AutoCrudBuilder<T> {
        private final Class<T> type;
        private CrudStore<T, ?> store;
        private TextResolver textResolver = new DefaultTextResolver();
        private Predicate<UiProperty<T>> propertyFilter = property -> true;
        private final Map<String, Consumer<UiPropertyConfig<T>>> overrides = new LinkedHashMap<>();
        private Class<?> actionProviderType;
        private final List<ResourceAction.ToolbarAction<T>> toolbarActions = new ArrayList<>();
        private final List<ResourceAction.RowAction<T>> rowActions = new ArrayList<>();
        private final List<ResourceAction.SelectionAction<T>> selectionActions = new ArrayList<>();

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
        public AutoCrudBuilder<T> withActionProvider(Class<?> actionProviderType) {
            this.actionProviderType = Objects.requireNonNull(actionProviderType, "actionProviderType");
            return this;
        }

        @Override
        public AutoCrudBuilder<T> toolbarAction(ResourceAction.ToolbarAction<T> action) {
            this.toolbarActions.add(Objects.requireNonNull(action, "action"));
            return this;
        }

        @Override
        public AutoCrudBuilder<T> rowAction(ResourceAction.RowAction<T> action) {
            this.rowActions.add(Objects.requireNonNull(action, "action"));
            return this;
        }

        @Override
        public AutoCrudBuilder<T> selectionAction(ResourceAction.SelectionAction<T> action) {
            this.selectionActions.add(Objects.requireNonNull(action, "action"));
            return this;
        }

        @Override
        public AutoCrudBuilder<T> override(String propertyName, Consumer<UiPropertyConfig<T>> config) {
            Objects.requireNonNull(propertyName, "propertyName");
            Objects.requireNonNull(config, "config");
            overrides.merge(propertyName, config, Consumer::andThen);
            return this;
        }

        @Override
        public GridFormsResourceView<T> build() {
            Objects.requireNonNull(store, "store");
            Map<String, UiPropertyConfig<T>> effectiveConfigs = buildEffectiveConfigs();
            String[] excluded = excludedPropertyNames(effectiveConfigs);
            Grid<T> grid = buildGrid(excluded, effectiveConfigs);
            return new GridFormsResourceView<>(
                    type,
                    store,
                    grid,
                    () -> buildForm(excluded, effectiveConfigs),
                    actionProviderType,
                    toolbarActions,
                    rowActions,
                    selectionActions
            );
        }

        private Grid<T> buildGrid(String[] excluded, Map<String, UiPropertyConfig<T>> configs) {
            Grids.AutoGridBuilder<T> builder = Grids.auto(type)
                    .withTextResolver(textResolver)
                    .exclude(excluded);
            overrides.keySet().forEach(propertyName -> {
                UiPropertyConfig<T> config = configs.get(propertyName);
                if (config == null || !config.hasLabelOverride()) {
                    return;
                }
                builder.override(propertyName, column -> column.setHeader(resolve(config.labelKey())));
            });
            return builder.build();
        }

        private Forms.Form<T> buildForm(String[] excluded, Map<String, UiPropertyConfig<T>> configs) {
            Forms.AutoFormBuilder<T> builder = Forms.auto(type)
                    .withTextResolver(textResolver)
                    .exclude(excluded);
            overrides.keySet().forEach(propertyName -> {
                UiPropertyConfig<T> config = configs.get(propertyName);
                if (config == null) {
                    return;
                }
                builder.override(propertyName, field -> {
                    if (config.hasLabelOverride() && field instanceof HasLabel hasLabel) {
                        hasLabel.setLabel(resolve(config.labelKey()));
                    }
                    if (config.hasRequiredOverride() && field instanceof HasValueAndElement<?, ?> valueAndElement) {
                        valueAndElement.setRequiredIndicatorVisible(config.isRequired());
                    }
                    if (config.hasTooltipOverride() && field instanceof Component component) {
                        component.getElement().setProperty("title", resolve(config.tooltipKey()));
                    }
                });
            });
            return builder.build();
        }

        private Map<String, UiPropertyConfig<T>> buildEffectiveConfigs() {
            UiMeta<T> uiMeta = UiInspector.inspect(type);
            Map<String, UiPropertyConfig<T>> configs = new LinkedHashMap<>();
            uiMeta.properties().forEach(property -> {
                UiPropertyConfig<T> config = new UiPropertyConfig<>(property);
                Consumer<UiPropertyConfig<T>> override = overrides.get(property.name());
                if (override != null) {
                    override.accept(config);
                }
                configs.put(property.name(), config);
            });

            List<String> unknownProperties = new ArrayList<>();
            for (String propertyName : overrides.keySet()) {
                if (!configs.containsKey(propertyName)) {
                    unknownProperties.add(propertyName);
                }
            }
            if (!unknownProperties.isEmpty()) {
                throw new IllegalArgumentException(
                        "Unknown property override(s) for type " + type.getName() + ": " + String.join(", ", unknownProperties)
                );
            }
            return configs;
        }

        private String[] excludedPropertyNames(Map<String, UiPropertyConfig<T>> configs) {
            return configs.values().stream()
                    .filter(config -> !config.isVisible() || !propertyFilter.test(config.property()))
                    .map(config -> config.property().name())
                    .toArray(String[]::new);
        }

        private String resolve(String key) {
            return textResolver.resolve(key).orElse(key);
        }
    }

    private static <T> String[] excludedPropertyNames(Class<T> type, Predicate<UiProperty<T>> propertyFilter) {
        return UiInspector.inspect(type).properties()
                .filter(property -> !propertyFilter.test(property))
                .map(UiProperty::name)
                .toArray(String[]::new);
    }
}
