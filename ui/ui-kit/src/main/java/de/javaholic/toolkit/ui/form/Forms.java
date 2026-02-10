package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.BeanValidator;
import de.javaholic.toolkit.i18n.I18n;
import de.javaholic.toolkit.ui.form.fields.FieldContext;
import de.javaholic.toolkit.ui.form.fields.FieldRegistry;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

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
     *              f.label("Name");
     *              f.validate(b -> b.asRequired("Required"));
     *          })
     *          .field("enabled", f -> {
     *              f.component(Inputs.checkbox().build());
     *              f.label("Enabled");
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
            List<FieldAccess> fields = FieldAccess.forType(type);
            Map<String, FieldAccess> byName = new LinkedHashMap<>();
            for (FieldAccess field : fields) {
                byName.put(field.name(), field);
            }
            failOnUnknownOverrides(byName.keySet());

            VerticalLayout layout = new VerticalLayout();
            Binder<T> binder = new Binder<>(type);
            Map<String, Component> components = new LinkedHashMap<>();

            for (FieldAccess field : fields) {
                FieldOverride<T> override = overrides.get(field.name());
                FieldSpec<T> spec = new FieldSpec<>();
                if (override != null) {
                    override.apply(spec);
                }

                Component component = spec.component;
                if (component == null) {
                    FieldContext ctx = new FieldContext(
                            type,
                            field.name(),
                            field.type(),
                            field.annotations()
                    );
                    component = fieldRegistry.create(ctx);
                }

                applyLabel(component, field.name(), spec.label);
                bindField(binder, field, component, spec.validators);

                layout.add(component);
                components.put(field.name(), component);
            }

            Form<T> form = new Form<>(layout, binder, components);
            for (Consumer<Form<T>> config : configurators) {
                config.accept(form);
            }
            return form;
        }

        private void failOnUnknownOverrides(Set<String> fieldNames) {
            for (String key : overrides.keySet()) {
                if (!fieldNames.contains(key)) {
                    throw new IllegalArgumentException(
                            "No such property '" + key + "' on " + type.getSimpleName()
                    );
                }
            }
        }

        private void applyLabel(Component component, String fieldName, String overrideLabel) {
            if (!(component instanceof HasLabel hasLabel)) {
                return;
            }
            String label = overrideLabel;
            if (label == null) {
                label = i18n != null ? i18n.text(fieldName) : fieldName;
            }
            hasLabel.setLabel(label);
        }

        private void bindField(
                Binder<T> binder,
                FieldAccess field,
                Component component,
                List<Consumer<Binder.BindingBuilder<T, Object>>> validators
        ) {
            if (!(component instanceof HasValue<?, ?>)) {
                throw new IllegalStateException(
                        "Component for property '" + field.name() + "' is not a HasValue"
                );
            }

            @SuppressWarnings("unchecked")
            HasValue<?, Object> value = (HasValue<?, Object>) component;
            Binder.BindingBuilder<T, Object> binding = binder.forField(value);

            binding = binding.withValidator(new BeanValidator(type, field.name()));
            for (Consumer<Binder.BindingBuilder<T, Object>> validator : validators) {
                validator.accept(binding);
            }
            binding.bind(
                    bean -> field.get(bean),
                    (bean, v) -> field.set(bean, v)
            );
        }

        /**
         * Internal collector for per-field override data.
         */
        static final class FieldSpec<T> implements FieldOverride.Applier<T> {
            Component component;
            String label;
            final List<Consumer<Binder.BindingBuilder<T, Object>>> validators = new ArrayList<>();

            @Override
            public void component(Component component) {
                this.component = component;
            }

            @Override
            public void label(String label) {
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

            void label(String label);

            void validate(Consumer<Binder.BindingBuilder<T, Object>> validator);
        }

        private Component component;
        private String label;
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
        public FieldOverride<T> label(String label) {
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

    /**
     * Reflection-based field access preserving declaration order.
     */
    static final class FieldAccess {

        private final Field field;
        private final AnnotatedElement annotations;

        private FieldAccess(Field field, AnnotatedElement annotations) {
            this.field = field;
            this.annotations = annotations;
            this.field.setAccessible(true);
        }

        static List<FieldAccess> forType(Class<?> type) {
            if (type.isRecord()) {
                return fromRecord(type);
            }
            return fromDeclaredFields(type);
        }

        String name() {
            return field.getName();
        }

        Class<?> type() {
            return field.getType();
        }

        AnnotatedElement annotations() {
            return annotations;
        }

        Object get(Object bean) {
            try {
                return field.get(bean);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        void set(Object bean, Object value) {
            try {
                field.set(bean, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private static List<FieldAccess> fromDeclaredFields(Class<?> type) {
            List<FieldAccess> fields = new ArrayList<>();
            for (Field field : type.getDeclaredFields()) {
                if (field.isSynthetic() || java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                fields.add(new FieldAccess(field, field));
            }
            return fields;
        }

        private static List<FieldAccess> fromRecord(Class<?> type) {
            List<FieldAccess> fields = new ArrayList<>();
            for (RecordComponent component : type.getRecordComponents()) {
                try {
                    Field field = type.getDeclaredField(component.getName());
                    fields.add(new FieldAccess(field, component));
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
            return fields;
        }
    }
}
