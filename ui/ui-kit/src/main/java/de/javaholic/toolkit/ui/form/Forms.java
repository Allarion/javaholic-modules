package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.validator.BeanValidator;
import de.javaholic.toolkit.i18n.DefaultTextResolver;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.introspection.BeanIntrospector;
import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;
import de.javaholic.toolkit.introspection.BeanPropertyTypes;
import de.javaholic.toolkit.iam.core.api.PermissionChecker;
import de.javaholic.toolkit.ui.form.fields.FieldContext;
import de.javaholic.toolkit.ui.form.fields.FieldRegistry;
import de.javaholic.toolkit.ui.form.state.BinderFormState;
import de.javaholic.toolkit.ui.form.state.FormState;
import de.javaholic.toolkit.ui.meta.UiInspector;
import de.javaholic.toolkit.ui.meta.UiMeta;
import de.javaholic.toolkit.ui.meta.UiProperty;
import de.javaholic.toolkit.ui.policy.DefaultUiPolicyEngine;
import de.javaholic.toolkit.ui.policy.UiDecision;
import de.javaholic.toolkit.ui.policy.UiPolicyContext;
import de.javaholic.toolkit.ui.policy.UiPolicyEngine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Form builder facade for Vaadin Binder-based forms.
 *
 * <p>Responsibility:</p>
 * <ul>
 * <li>Provide fluent manual form construction via {@link #of(Class)}</li>
 * <li>Provide convention-based auto form creation via {@link #auto(Class)} backed by {@link UiMeta}</li>
 * </ul>
 *
 * <p>Must not do: define UI metadata policy inside form rendering code. Grid/Form layers must consume
 * {@link UiMeta} rather than performing their own bean-introspection rules for visibility/labels/order.</p>
 *
 * <p>Architecture fit: rendering/binding layer. It binds components using metadata prepared by the
 * introspection/meta layers and keeps policy decisions outside Vaadin component wiring.</p>
 *
 * <p>Manual example:</p>
 * <pre>{@code
 * Forms.Form<User> manual = Forms.of(User.class)
 *     .field("email", f -> f.label("user.email"))
 *     .build();
 * }</pre>
 *
 * <p>Auto example:</p>
 * <pre>{@code
 * Forms.Form<User> auto = Forms.auto(User.class).build();
 * }</pre>
 *
 * <p>Auto with overrides:</p>
 * <pre>{@code
 * Forms.Form<User> customized = Forms.auto(User.class)
 *     .exclude("password")
 *     .override("email", field -> field.setReadOnly(true))
 *     .build();
 * }</pre>
 *
 * <p>Auto with annotation + text resolver:</p>
 * <pre>{@code
 * Forms.Form<User> form = Forms.auto(User.class)
 *     .withTextResolver(key -> messages.getOrDefault(key, key))
 *     .build();
 * }</pre>
 */
public final class Forms {

    private Forms() {
    }

    /**
     * Entry point for fluent form creation.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * Forms.Form<UserDTO> form =
     *     Forms.of(UserDTO.class)
     *          .withTextResolver(key -> key)
     *          .withValidation()
     *          .build();
     *
     * add(form.layout());
     * }</pre>
     */
    public static <T> FormBuilder<T> of(Class<T> type) {
        return new FormBuilder<>(type);
    }

    /**
     * Entry point for convention-based auto form creation using {@link UiMeta}.
     *
     * <p>Supports semantic annotations like {@code @UiHidden}, {@code @UiLabel},
     * {@code @UiOrder}, and {@code @UiReadOnly} through UiMeta.</p>
     */
    public static <T> AutoFormBuilder<T> auto(Class<T> type) {
        return new AutoFormBuilder<>(type);
    }

    /**
     * Creates a reactive state facade for a Binder-backed form.
     */
    public static <T> FormState state(Binder<T> binder) {
        return new BinderFormState<>(binder);
    }

    /**
     * Fluent builder for a convention-based Vaadin form.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * Forms.Form<UserConfig> form =
     *     Forms.of(UserConfig.class)
     *          .field("name", f -> {
     *              f.component(Inputs.textField().widthFull().build());
     *              f.label("user.name");
     *              f.validate(b -> b.asRequired("user.name.required"));
     *          })
     *          .field("enabled", f -> {
     *              f.component(Inputs.checkbox().build());
     *              f.label("user.enabled");
     *          })
     *          .build();
     * }</pre>
     */
    public static final class FormBuilder<T> {

        private final Class<T> type;
        private final FieldRegistry fieldRegistry = new FieldRegistry();
        // UI boundary: FormBuilder stores label keys and resolves them while wiring Vaadin fields.
        private TextResolver textResolver = new DefaultTextResolver();
        private final Map<String, FieldOverride<T>> overrides = new LinkedHashMap<>();
        private final List<Consumer<Form<T>>> configurators = new ArrayList<>();
        private boolean includeId = false;
        private boolean includeVersion = false;

        private FormBuilder(Class<T> type) {
            this.type = Objects.requireNonNull(type, "type");
        }

        /**
         * Sets a key resolver for labels.
         */
        public FormBuilder<T> withTextResolver(TextResolver textResolver) {
            this.textResolver = Objects.requireNonNull(textResolver, "textResolver");
            return this;
        }

        /**
         * Bean Validation is always active; this is kept for fluent symmetry.
         * TODO: why not remove this if always active anyways??
         *
         * <p>Example: {@code Forms.of(User.class).withValidation().build();}</p>
         */
        @Deprecated
        public FormBuilder<T> withValidation() {
            return this;
        }

        /**
         * Overrides a single field by name.
         *
         * <p>Example: {@code Forms.of(User.class).field("email", f -> f.label("user.email"));}</p>
         */
        public FormBuilder<T> field(String name, Consumer<FieldOverride<T>> spec) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(spec, "spec");
            FieldOverride<T> override = overrides.computeIfAbsent(name, key -> new FieldOverride<>());
            spec.accept(override);
            return this;
        }

        /**
         * Includes the technical {@code @Id} property in manual form mode.
         *
         * <p>Example: {@code Forms.of(User.class).includeId().build();}</p>
         */
        // TODO: revisit, vgl @UiHidden usw
        public FormBuilder<T> includeId() {
            this.includeId = true;
            return this;
        }

        /**
         * Includes the technical {@code @Version} property in manual form mode.
         *
         * <p>Example: {@code Forms.of(User.class).includeVersion().build();}</p>
         */
        // TODO: revisit, vgl @UiHidden usw
        public FormBuilder<T> includeVersion() {
            this.includeVersion = true;
            return this;
        }

        /**
         * Conditionally includes technical fields based on a supplier.
         *
         * <p>Example: {@code Forms.of(User.class).includeTechnicalFields(() -> debugMode).build();}</p>
         */
        public FormBuilder<T> includeTechnicalFields(Supplier<Boolean> include) {
            if (include.get()) {
                includeId();
                includeVersion();
            }
            return this;
        }

        /**
         * Applies additional configuration to the built form.
         *
         * <p>Example: {@code Forms.of(User.class).configure(f -> f.layout().addClassName("user-form"));}</p>
         */
        public FormBuilder<T> configure(Consumer<Form<T>> config) {
            Objects.requireNonNull(config, "config");
            configurators.add(config);
            return this;
        }

        /**
         * Builds the form in a single pass (override first, then auto field).
         *
         * <p>Example: {@code Forms.Form<User> form = Forms.of(User.class).build();}</p>
         */
        public Form<T> build() {
            BeanMeta<T> meta = BeanIntrospector.inspect(type);
            UiMeta<T> uiMeta = UiInspector.inspect(type);
            Set<String> hiddenByDefault = uiMeta.properties()
                    .filter(property -> !property.isVisible())
                    .map(UiProperty::name)
                    .collect(LinkedHashSet::new, Set::add, Set::addAll);
            VerticalLayout layout = new VerticalLayout();
            BeanValidationBinder<T> binder = new BeanValidationBinder<>(type);
            Map<String, Component> components = new LinkedHashMap<>();
            Map<String, Boolean> uiRequiredByName = uiMeta.properties()
                    .collect(LinkedHashMap::new, (map, property) -> map.put(property.name(), property.isRequired()), Map::putAll);

            String formError = resolve("form.validation.error");
            Span formErrorLabel = new Span(formError != null ? formError : "form.validation.error");
            formErrorLabel.addClassName("form-error");
            formErrorLabel.setVisible(false);
            layout.add(formErrorLabel);

            binder.addStatusChangeListener(event -> formErrorLabel.setVisible(event.hasValidationErrors()));
            // TODO: unit test isIncludedByTechnicalFlags
            for (BeanProperty<T, ?> property : meta.properties()) {
                if (!isIncludedByTechnicalFlags(property, meta, hiddenByDefault)) {
                    continue;
                }

                FieldOverride<T> override = overrides.get(property.name());
                FieldSpec<T> spec = new FieldSpec<>();
                if (override != null) {
                    override.apply(spec);
                }

                Component component = spec.component;
                HasValue<?, ?> value;
                if (component == null) {
                    FieldContext ctx = new FieldContext(type, property.name(), property.type(), null, property.definition());
                    value = fieldRegistry.create(ctx);
                    if (!(value instanceof Component)) {
                        throw new IllegalStateException("FieldFactory returned non-Component for property '" + property.name() + "'");
                    }
                    component = (Component) value;
                } else if (component instanceof HasValue<?, ?> hasValue) {
                    value = hasValue;
                } else {
                    throw new IllegalStateException("Component for property '" + property.name() + "' is not a HasValue");
                }

                applyLabel(component, property.name(), spec.labelKey);
                boolean uiRequired = Boolean.TRUE.equals(uiRequiredByName.get(property.name()));
                applyRequiredIndicator(component, property.definition(), uiRequired);
                bindUntyped(
                        binder,
                        meta,
                        property,
                        value,
                        spec.validators,
                        uiRequired,
                        requiredMessage(property.name(), spec.labelKey)
                );

                layout.add(component);
                components.put(property.name(), component);
            }

            Form<T> form = new Form<>(layout, binder, components);
            for (Consumer<Form<T>> config : configurators) {
                config.accept(form);
            }
            return form;
        }

        private boolean isIncludedByTechnicalFlags(
                BeanProperty<T, ?> property,
                BeanMeta<T> meta,
                Set<String> hiddenByDefault
        ) {
            if (!hiddenByDefault.contains(property.name())) {
                return true;
            }
            boolean isId = meta.idProperty().map(p -> p.name().equals(property.name())).orElse(false);
            if (isId) {
                return includeId;
            }
            boolean isVersion = meta.versionProperty().map(p -> p.name().equals(property.name())).orElse(false);
            if (isVersion) {
                return includeVersion;
            }
            return false;
        }

        private void applyLabel(Component component, String fieldName, String overrideLabelKey) {
            if (!(component instanceof HasLabel hasLabel)) {
                return;
            }
            String labelKey = overrideLabelKey != null ? overrideLabelKey : fieldName;
            String resolved = textResolver.resolve(labelKey).orElse(labelKey);
            hasLabel.setLabel(resolved != null ? resolved : labelKey);
        }

        private String resolve(String key) {
            String resolved = textResolver.resolve(key).orElse(key);
            return resolved != null ? resolved : key;
        }

        private String requiredMessage(String fieldName, String overrideLabelKey) {
            String labelKey = overrideLabelKey != null ? overrideLabelKey : fieldName;
            String label = resolve(labelKey);
            return label + " required";
        }

        private void applyRequiredIndicator(Component component, AnnotatedElement annotations, boolean uiRequired) {
            if (!uiRequired && !isRequired(annotations)) {
                return;
            }
            if (component instanceof HasValueAndElement hasValue) {
                hasValue.setRequiredIndicatorVisible(true);
            }
        }

        @SuppressWarnings("unchecked")
        private <V> void bindUntyped(
                BeanValidationBinder<T> binder,
                BeanMeta<T> meta,
                BeanProperty<T, ?> property,
                HasValue<?, ?> field,
                List<TypedValidator<T>> validators,
                boolean uiRequired,
                String uiRequiredMessage
        ) {
            //noinspection rawtypes
            bindField(
                    binder,
                    meta,
                    (BeanProperty<T, V>) property,
                    (HasValue<?, V>) field,
                    (List) validators,
                    uiRequired,
                    uiRequiredMessage
            );
        }

        private <V> void bindField(
                BeanValidationBinder<T> binder,
                BeanMeta<T> meta,
                BeanProperty<T, V> property,
                HasValue<?, V> fieldComponent,
                List<TypedValidator<T>> validators,
                boolean uiRequired,
                String uiRequiredMessage
        ) {
            Binder.BindingBuilder<T, V> binding = binder.forField(fieldComponent);
            if (fieldComponent instanceof HasValidation hasValidation) {
                binding = binding.withValidationStatusHandler(status -> {
                    hasValidation.setInvalid(status.isError());
                    hasValidation.setErrorMessage(status.getMessage().orElse(null));
                });
            }

            if (uiRequired) {
                binding = binding.asRequired(uiRequiredMessage);
            }

            BeanValidator beanValidator = new BeanValidator(type, property.name());
            if (uiRequired) {
                binding = binding.withValidator((value, context) ->
                        isEmptyValue(value) ? ValidationResult.ok() : beanValidator.apply(value, context)
                );
            } else {
                binding = binding.withValidator(beanValidator);
            }

            for (TypedValidator<T> v : validators) {

                if (!v.valueType().equals(property.type())) {
                    throw new IllegalArgumentException(
                            "Validator type mismatch for property '" + property.name() + "'"
                    );
                }

                @SuppressWarnings("unchecked")
                Consumer<Binder.BindingBuilder<T, V>> consumer =
                        (Consumer<Binder.BindingBuilder<T, V>>) v.consumer();

                consumer.accept(binding);
            }

            binding.bind(
                    bean -> meta.getValue(property, bean),
                    (bean, val) -> meta.setValue(property, bean, val)
            );
        }


        /**
         * Internal collector for per-field override data.
         */
        static final class FieldSpec<T> implements FieldOverride.Applier<T> {
            Component component;
            String labelKey;
            final List<TypedValidator<T>> validators = new ArrayList<>();

            @Override
            public void validate(TypedValidator<T> validator) {
                validators.add(validator); // controlled unsafe cast
            }

            @Override
            public void component(Component component) {
                this.component = component;
            }

            @Override
            public void label(String labelKey) {
                this.labelKey = labelKey;
            }
        }
    }

    /**
     * Convention-based form builder that consumes {@link UiMeta}.
     *
     * <p>Example:</p>
     * <pre>{@code
     * Forms.Form<User> form = Forms.auto(User.class)
     *     .exclude("password")
     *     .build();
     * }</pre>
     */
    public static final class AutoFormBuilder<T> {
        private final Class<T> type;
        private final UiMeta<T> uiMeta;
        private FieldRegistry fieldRegistry = new FieldRegistry();
        private PermissionChecker permissionChecker;
        // UiMeta provides keys only; auto forms resolve keys only while rendering fields.
        private TextResolver textResolver = new DefaultTextResolver();
        private final Set<String> excluded = new LinkedHashSet<>();
        private final Map<String, Consumer<HasValue<?, ?>>> overrides = new LinkedHashMap<>();
        private final List<Consumer<Form<T>>> configurators = new ArrayList<>();

        private AutoFormBuilder(Class<T> type) {
            this.type = Objects.requireNonNull(type, "type");
            // ------------------------------------------------------------------
            // UI semantic annotation evaluation happens *only* in UiMeta.
            // Label resolution to actual display text happens *only* via TextResolver.
            // FieldRegistry consumes resolved attributes, but never evaluates annotations.
            // ------------------------------------------------------------------
            this.uiMeta = UiInspector.inspect(type);
        }

        /**
         * Sets a custom resolver for semantic text keys.
         *
         * <p>Example:</p>
         * <pre>{@code
         * Forms.auto(User.class)
         *     .withTextResolver(key -> bundle.getString(key))
         *     .build();
         * }</pre>
         */
        public AutoFormBuilder<T> withTextResolver(TextResolver textResolver) {
            this.textResolver = Objects.requireNonNull(textResolver, "textResolver");
            return this;
        }

        /**
         * Sets the permission checker used by {@link UiPolicyEngine} for {@code @UiPermission} visibility decisions.
         */
        public AutoFormBuilder<T> withPermissionChecker(PermissionChecker permissionChecker) {
            this.permissionChecker = permissionChecker;
            return this;
        }

        /**
         * Excludes properties from auto-generated fields.
         *
         * <p>Example: {@code Forms.auto(User.class).exclude("password").build();}</p>
         */
        public AutoFormBuilder<T> exclude(String... propertyNames) {
            if (propertyNames == null) {
                return this;
            }
            Arrays.stream(propertyNames)
                    .filter(Objects::nonNull)
                    .forEach(excluded::add);
            return this;
        }

        /**
         * Applies field customization for one auto-generated property.
         *
         * <p>Example: {@code Forms.auto(User.class).override("email", v -> v.setReadOnly(true)).build();}</p>
         */
        public AutoFormBuilder<T> override(String propertyName, Consumer<HasValue<?, ?>> customizer) {
            Objects.requireNonNull(propertyName, "propertyName");
            Objects.requireNonNull(customizer, "customizer");
            overrides.merge(propertyName, customizer, Consumer::andThen);
            return this;
        }

        /**
         * Applies additional configuration to the final built form.
         *
         * <p>Example: {@code Forms.auto(User.class).configure(f -> f.layout().setWidth("600px")).build();}</p>
         */
        public AutoFormBuilder<T> configure(Consumer<Form<T>> config) {
            Objects.requireNonNull(config, "config");
            configurators.add(config);
            return this;
        }

        /**
         * Builds the auto form from {@link UiMeta} and default field mappings.
         *
         * <p>Example: {@code Forms.Form<User> form = Forms.auto(User.class).build();}</p>
         */
        public Form<T> build() {
            // UiMeta is the source of UI semantics; BeanMeta is accessed only through this boundary object.
            BeanMeta<T> beanMeta = uiMeta.beanMeta();
            Map<String, BeanProperty<T, ?>> beanProperties = beanMeta.properties().stream()
                    .collect(LinkedHashMap::new, (map, prop) -> map.put(prop.name(), prop), Map::putAll);

            VerticalLayout layout = new VerticalLayout();
            Map<String, Component> components = new LinkedHashMap<>();
            Map<String, UiProperty<T>> uiPropertiesByName = new LinkedHashMap<>();
            UiPolicyEngine policyEngine = new DefaultUiPolicyEngine();
            Consumer<T> policyApplier = bean -> applyUiPolicy(
                    uiPropertiesByName,
                    components,
                    policyEngine,
                    new UiPolicyContext(permissionChecker, bean)
            );
            BeanValidationBinder<T> binder = new PolicyAwareBeanValidationBinder<>(type, policyApplier);
            // TODO: revisit i18n key, see HierarchicalTextResolver for concept
            String formError = textResolver.resolve("form.validation.error").orElse("form.validation.error");
            Span formErrorLabel = new Span(formError);
            formErrorLabel.addClassName("form-error");
            formErrorLabel.setVisible(false);
            layout.add(formErrorLabel);

            binder.addStatusChangeListener(event -> formErrorLabel.setVisible(event.hasValidationErrors()));

            uiMeta.properties()
                    .filter(UiProperty::isVisible)
                    .filter(property -> !excluded.contains(property.name()))
                    .sorted(Comparator.comparingInt(UiProperty::order))
                    .forEach(property -> {
                        uiPropertiesByName.put(property.name(), property);
                        addField(property, beanMeta, beanProperties, layout, binder, components);
                    });

            Form<T> form = new Form<>(layout, binder, components);
            // UI policy integration point: evaluate UiProperty metadata into runtime component state.
            policyApplier.accept(binder.getBean());
            for (Consumer<Form<T>> config : configurators) {
                config.accept(form);
            }
            return form;
        }

        private void applyUiPolicy(
                Map<String, UiProperty<T>> uiPropertiesByName,
                Map<String, Component> components,
                UiPolicyEngine engine,
                UiPolicyContext context
        ) {
            uiPropertiesByName.forEach((name, property) -> {
                Component component = components.get(name);
                if (component == null) {
                    return;
                }
                UiDecision decision = engine.evaluate(property, context);
                component.setVisible(decision.visible());
                if (component instanceof HasEnabled hasEnabled) {
                    hasEnabled.setEnabled(decision.enabled());
                }
                if (decision.readOnly() && component instanceof HasValue<?, ?> hasValue) {
                    hasValue.setReadOnly(true);
                }
                if (decision.required() && component instanceof HasValueAndElement<?, ?> hasValueAndElement) {
                    hasValueAndElement.setRequiredIndicatorVisible(true);
                }
            });
        }

        @SuppressWarnings("unchecked")
        private void addField(
                UiProperty<T> property,
                BeanMeta<T> beanMeta,
                Map<String, BeanProperty<T, ?>> beanProperties,
                VerticalLayout layout,
                BeanValidationBinder<T> binder,
                Map<String, Component> components
        ) {
            BeanProperty<T, ?> beanProperty = beanProperties.get(property.name());
            if (beanProperty == null) {
                throw new IllegalStateException("Unknown BeanProperty for UiProperty '" + property.name() + "'");
            }

            Class<?> elementType = BeanPropertyTypes.resolveCollectionElementType(type, beanProperty);
            FieldContext ctx = new FieldContext(type, property.name(), property.type(), elementType, beanProperty.definition());
            HasValue<?, ?> value = fieldRegistry.create(ctx, property.labelKey(), property.isReadOnly());
            if (!(value instanceof Component component)) {
                throw new IllegalStateException("FieldFactory returned non-Component for property '" + property.name() + "'");
            }

            applyLabel(component, property.labelKey());
            applyRequiredIndicator(component, beanProperty.definition(), property.isRequired());

            Consumer<HasValue<?, ?>> override = overrides.get(property.name());
            if (override != null) {
                override.accept(value);
            }

            bindAutoFieldUntyped(
                    binder,
                    beanMeta,
                    beanProperty,
                    value,
                    property.isRequired(),
                    textResolver.resolve(property.labelKey()).orElse(property.labelKey()) + " required"
            );

            layout.add(component);
            components.put(property.name(), component);
        }
        private void applyLabel(Component component, String labelKey) {
            if (!(component instanceof HasLabel hasLabel)) {
                return;
            }
            hasLabel.setLabel(textResolver.resolve(labelKey).orElse(labelKey));
        }

        private void applyRequiredIndicator(Component component, AnnotatedElement annotations, boolean uiRequired) {
            if (!uiRequired && !isRequired(annotations)) {
                return;
            }
            if (component instanceof HasValueAndElement hasValue) {
                hasValue.setRequiredIndicatorVisible(true);
            }
        }

        @SuppressWarnings("unchecked")
        private <V> void bindAutoFieldUntyped(
                BeanValidationBinder<T> binder,
                BeanMeta<T> meta,
                BeanProperty<T, ?> property,
                HasValue<?, ?> field,
                boolean uiRequired,
                String uiRequiredMessage
        ) {
            bindAutoField(
                    binder,
                    meta,
                    (BeanProperty<T, V>) property,
                    (HasValue<?, V>) field,
                    uiRequired,
                    uiRequiredMessage
            );
        }

        private <V> void bindAutoField(
                BeanValidationBinder<T> binder,
                BeanMeta<T> meta,
                BeanProperty<T, V> property,
                HasValue<?, V> fieldComponent,
                boolean uiRequired,
                String uiRequiredMessage
        ) {
            Binder.BindingBuilder<T, V> binding = binder.forField(fieldComponent);
            if (fieldComponent instanceof HasValidation hasValidation) {
                binding = binding.withValidationStatusHandler(status -> {
                    hasValidation.setInvalid(status.isError());
                    hasValidation.setErrorMessage(status.getMessage().orElse(null));
                });
            }
            if (uiRequired) {
                binding = binding.asRequired(uiRequiredMessage);
            }
            BeanValidator beanValidator = new BeanValidator(type, property.name());
            if (uiRequired) {
                binding = binding.withValidator((value, context) ->
                        isEmptyValue(value) ? ValidationResult.ok() : beanValidator.apply(value, context)
                );
            } else {
                binding = binding.withValidator(beanValidator);
            }
            binding.bind(
                    bean -> meta.getValue(property, bean),
                    (bean, val) -> meta.setValue(property, bean, val)
            );
        }

        private static final class PolicyAwareBeanValidationBinder<T> extends BeanValidationBinder<T> {

            private final Consumer<T> onBeanChanged;

            private PolicyAwareBeanValidationBinder(Class<T> beanType, Consumer<T> onBeanChanged) {
                super(beanType);
                this.onBeanChanged = Objects.requireNonNull(onBeanChanged, "onBeanChanged");
            }

            @Override
            public void setBean(T bean) {
                super.setBean(bean);
                onBeanChanged.accept(bean);
            }
        }
    }

    /**
     * Per-field customization object used by {@link FormBuilder#field(String, Consumer)}.
     *
     * <p>Example: {@code .field("email", f -> f.component(Inputs.emailField().build()))}</p>
     */
    public static final class FieldOverride<T> {

        /**
         * Internal contract for applying field overrides to a build step.
         */
        interface Applier<T> {
            void component(Component component);

            void label(String labelKey);

            void validate(TypedValidator<T> validator);
        }

        private Component component;
        private String labelKey;
        private final List<TypedValidator<T>> validators = new ArrayList<>();

        /**
         * Sets the concrete component to use for this field.
         *
         * <p>Example: {@code f.component(Inputs.textField().build());}</p>
         */
        public FieldOverride<T> component(Component component) {
            this.component = Objects.requireNonNull(component, "component");
            return this;
        }

        /**
         * Sets the label for this field.
         *
         * <p>Example: {@code f.label("user.email");}</p>
         */
        public FieldOverride<T> label(String labelKey) {
            this.labelKey = Objects.requireNonNull(labelKey, "labelKey");
            return this;
        }

        /**
         * Adds an additional validator to the field binding.
         *
         * <p>Example: {@code f.validate(String.class, b -> b.asRequired("Email required"));}</p>
         */
        // TODO: revisit specifying the valueType here. should be somehow derivied from the BeanMeta <- OLD COMMENT!! UiMeta is the correct Model; still: 'Class<V> valueType' has to go
        public <V> FieldOverride<T> validate(Class<V> valueType, Consumer<Binder.BindingBuilder<T, V>> validator) {
            validators.add(new TypedValidator<>(valueType, validator));
            return this;
        }

        void apply(Applier<T> applier) {
            if (component != null) {
                applier.component(component);
            }
            if (labelKey != null) {
                applier.label(labelKey);
            }
            for (TypedValidator<T> v : validators) {
                applier.validate(v);
            }
        }
    }

    record TypedValidator<T>(Class<?> valueType, Consumer<?> consumer) {
    }

    /**
     * Built form result containing root layout, binder, and named field map.
     *
     * <p>Example: {@code Forms.Form<User> form = Forms.auto(User.class).build();}</p>
     */
    public static final class Form<T> {

        private final Component layout;
        private final Binder<T> binder;
        private final Map<String, Component> fields;

        private Form(Component layout, Binder<T> binder, Map<String, Component> fields) {
            this.layout = layout;
            this.binder = binder;
            this.fields = Collections.unmodifiableMap(new LinkedHashMap<>(fields));
        }

        /**
         * Root layout component containing all field components.
         *
         * <p>Example: {@code add(form.layout());}</p>
         */
        public Component layout() {
            return layout;
        }

        /**
         * Binder used for field bindings and validation.
         *
         * <p>Example: {@code form.binder().setBean(user);}</p>
         */
        public Binder<T> binder() {
            return binder;
        }

        /**
         * Returns a field component by name, if present.
         *
         * <p>Example: {@code form.field("email").ifPresent(c -> c.setVisible(true));}</p>
         */
        public Optional<Component> field(String name) {
            return Optional.ofNullable(fields.get(name));
        }
    }

    private static boolean isRequired(AnnotatedElement annotations) {
        return annotations.isAnnotationPresent(NotNull.class) || annotations.isAnnotationPresent(NotBlank.class) || annotations.isAnnotationPresent(NotEmpty.class);
    }

    private static boolean isEmptyValue(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof CharSequence chars) {
            return chars.toString().trim().isEmpty();
        }
        if (value instanceof Collection<?> collection) {
            return collection.isEmpty();
        }
        if (value instanceof Map<?, ?> map) {
            return map.isEmpty();
        }
        return false;
    }
}
