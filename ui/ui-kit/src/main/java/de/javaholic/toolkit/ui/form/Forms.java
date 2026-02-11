package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.BeanValidator;
import de.javaholic.toolkit.i18n.I18n;
import de.javaholic.toolkit.i18n.Text;
import de.javaholic.toolkit.i18n.Texts;
import de.javaholic.toolkit.introspection.BeanIntrospector;
import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;
import de.javaholic.toolkit.ui.form.fields.FieldContext;
import de.javaholic.toolkit.ui.form.fields.FieldRegistry;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
     *          .withFieldRegistry(registry)
     *          .withI18n(i18n)
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
     * Fluent builder for a convention-based Vaadin form.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * Forms.Form<UserConfig> form =
     *     Forms.of(UserConfig.class)
     *          .field("name", f -> {
     *              f.component(Inputs.text().widthFull().build());
     *              f.label(Texts.label("user.name"));
     *              f.validate(b -> b.asRequired(Texts.resolve(i18n, Texts.error("user.name.required"))));
     *          })
     *          .field("enabled", f -> {
     *              f.component(Inputs.checkbox().build());
     *              f.label(Texts.label("user.enabled"));
     *          })
     *          .build();
     * }</pre>
     */
    public static final class FormBuilder<T> {

        private final Class<T> type;
        private FieldRegistry fieldRegistry = new FieldRegistry();
        private I18n i18n;
        private final Map<String, FieldOverride<T>> overrides = new LinkedHashMap<>();
        private final List<Consumer<Form<T>>> configurators = new ArrayList<>();
        private boolean includeId = false;
        private boolean includeVersion = false;

        private FormBuilder(Class<T> type) {
            this.type = Objects.requireNonNull(type, "type");
        }

        /**
         * Sets the FieldRegistry used for auto field creation.
         */
        public FormBuilder<T> withFieldRegistry(FieldRegistry fieldRegistry) {
            this.fieldRegistry = Objects.requireNonNull(fieldRegistry, "fieldRegistry");
            return this;
        }

        /**
         * Enables i18n for labels. If not set, the field name is used as label.
         */
        public FormBuilder<T> withI18n(I18n i18n) {
            this.i18n = Objects.requireNonNull(i18n, "i18n");
            return this;
        }

        /**
         * Bean Validation is always active; this is kept for fluent symmetry.
         */
        @Deprecated
        public FormBuilder<T> withValidation() {
            return this;
        }

        /**
         * Overrides a single field by name.
         */
        public FormBuilder<T> field(String name, Consumer<FieldOverride<T>> spec) {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(spec, "spec");
            FieldOverride<T> override = overrides.computeIfAbsent(name, key -> new FieldOverride<>());
            spec.accept(override);
            return this;
        }


        public FormBuilder<T> includeId() { this.includeId= true; return this; }
        public FormBuilder<T> includeVersion() { this.includeVersion= true; return this;  }
        public FormBuilder<T> includeTechnicalFields(Supplier<Boolean> include){
            if (include.get()){
                includeId();
                includeVersion();
            }
            return this;
        }

        /**
         * Applies additional configuration to the built form.
         */
        public FormBuilder<T> configure(Consumer<Form<T>> config) {
            Objects.requireNonNull(config, "config");
            configurators.add(config);
            return this;
        }

        /**
         * Builds the form in a single pass (override first, then auto field).
         */
        public Form<T> build() {
            BeanMeta<T> meta = BeanIntrospector.inspect(type);
            VerticalLayout layout = new VerticalLayout();
            Binder<T> binder = new Binder<>(type);
            Map<String, Component> components = new LinkedHashMap<>();

            Span formErrorLabel = new Span(
                    i18n != null
                            ? i18n.text("form.validation.error")
                            : "Please fix the highlighted fields"
            );
            formErrorLabel.addClassName("form-error");
            formErrorLabel.setVisible(false);
            layout.add(formErrorLabel);

            binder.addStatusChangeListener(event ->
                    formErrorLabel.setVisible(event.hasValidationErrors())
            );

            for (BeanProperty property : meta.properties()) {

                if (!includeId && meta.idProperty().map(p -> p.name().equals(property.name())).orElse(false)) {
                    continue;
                }

                if (!includeVersion && meta.versionProperty().map(p -> p.name().equals(property.name())).orElse(false)) {
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
                    FieldContext ctx = new FieldContext(
                            type,
                            property.name(),
                            property.type(),
                            property.definition()
                    );
                    value = fieldRegistry.create(ctx);
                    if (!(value instanceof Component)) {
                        throw new IllegalStateException(
                                "FieldFactory returned non-Component for property '" + property.name() + "'"
                        );
                    }
                    component = (Component) value;
                } else if (component instanceof HasValue<?, ?> hasValue) {
                    value = hasValue;
                } else {
                    throw new IllegalStateException(
                            "Component for property '" + property.name() + "' is not a HasValue"
                    );
                }

                applyLabel(component, property.name(), spec.label);
                applyRequiredIndicator(component, property.definition());
                bindField(binder, meta, property, value, spec.validators);

                layout.add(component);
                components.put(property.name(), component);
            }

            Form<T> form = new Form<>(layout, binder, components);
            for (Consumer<Form<T>> config : configurators) {
                config.accept(form);
            }
            return form;
        }

        private void applyLabel(Component component, String fieldName, Text overrideLabel) {
            if (!(component instanceof HasLabel hasLabel)) {
                return;
            }
            Text label = overrideLabel != null ? overrideLabel : Texts.label(fieldName);
            hasLabel.setLabel(Texts.resolve(i18n, label));
        }

        private void applyRequiredIndicator(Component component, AnnotatedElement annotations) {
            if (!isRequired(annotations)) {
                return;
            }
            if (component instanceof HasValueAndElement hasValue) {
                hasValue.setRequiredIndicatorVisible(true);
            }
        }

        private void bindField(
                Binder<T> binder,
                BeanMeta<T> meta,
                BeanProperty property,
                HasValue<?, ?> fieldComponent,
                List<Consumer<Binder.BindingBuilder<T, Object>>> validators
        ) {
            @SuppressWarnings("unchecked")
            HasValue<?, Object> value = (HasValue<?, Object>) fieldComponent;
            Binder.BindingBuilder<T, Object> binding = binder.forField(value);

            binding = binding.withValidator(new BeanValidator(type, property.name()));
            for (Consumer<Binder.BindingBuilder<T, Object>> validator : validators) {
                validator.accept(binding);
            }

            binding.bind(
                    bean -> meta.getValue(property, bean),
                    (bean, v) -> meta.setValue(property, bean, v)
            );
        }

        /**
         * Internal collector for per-field override data.
         */
        static final class FieldSpec<T> implements FieldOverride.Applier<T> {
            Component component;
            Text label;
            final List<Consumer<Binder.BindingBuilder<T, Object>>> validators = new ArrayList<>();

            @Override
            public void component(Component component) {
                this.component = component;
            }

            @Override
            public void label(Text label) {
                this.label = label;
            }

            @Override
            public void validate(Consumer<Binder.BindingBuilder<T, Object>> validator) {
                validators.add(validator);
            }
        }
    }

    public static final class FieldOverride<T> {

        /**
         * Internal contract for applying field overrides to a build step.
         */
        interface Applier<T> {
            void component(Component component);

            void label(Text label);

            void validate(Consumer<Binder.BindingBuilder<T, Object>> validator);
        }

        private Component component;
        private Text label;
        private final List<Consumer<Binder.BindingBuilder<T, Object>>> validators = new ArrayList<>();

        /**
         * Sets the concrete component to use for this field.
         */
        public FieldOverride<T> component(Component component) {
            this.component = Objects.requireNonNull(component, "component");
            return this;
        }

        /**
         * Sets the label for this field.
         */
        public FieldOverride<T> label(Text label) {
            this.label = Objects.requireNonNull(label, "label");
            return this;
        }

        /**
         * Adds an additional validator to the field binding.
         */
        public FieldOverride<T> validate(Consumer<Binder.BindingBuilder<T, Object>> validator) {
            validators.add(Objects.requireNonNull(validator, "validator"));
            return this;
        }

        void apply(Applier<T> applier) {
            if (component != null) {
                applier.component(component);
            }
            if (label != null) {
                applier.label(label);
            }
            for (Consumer<Binder.BindingBuilder<T, Object>> validator : validators) {
                applier.validate(validator);
            }
        }
    }

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
         */
        public Component layout() {
            return layout;
        }

        /**
         * Binder used for field bindings and validation.
         */
        public Binder<T> binder() {
            return binder;
        }

        /**
         * Returns a field component by name, if present.
         */
        public Optional<Component> field(String name) {
            return Optional.ofNullable(fields.get(name));
        }
    }

    private static boolean isRequired(AnnotatedElement annotations) {
        return annotations.isAnnotationPresent(NotNull.class) ||
                annotations.isAnnotationPresent(NotBlank.class) ||
                annotations.isAnnotationPresent(NotEmpty.class);
    }
}
