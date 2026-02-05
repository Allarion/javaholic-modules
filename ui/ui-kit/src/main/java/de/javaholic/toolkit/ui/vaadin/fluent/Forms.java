package de.javaholic.util.ui.vaadin.fluent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.validator.BeanValidator;

import de.javaholic.util.ui.i18n.I18n;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *  fluent builder for Vaadin forms.
 *
 * Example:
 *
 *   DefaultForm<UserConfig> form =
 *     Forms.of(UserConfig.class)
 *          .field("name")
 *              .component(Inputs.text().widthFull().build())
 *              .label("Name")
 *              .validate(b -> b.asRequired("Required"))
 *          .field("enabled")
 *              .component(Inputs.checkbox().build())
 *              .label("Enabled")
 *          .build();
 *
 * Bean Validation annotations on the bean are supported:
 *
 *   public class UserConfig {
 *       @NotNull
 *       @Size(min = 3, max = 40)
 *       private String name;
 *
 *       @Min(0)
 *       @Max(10)
 *       private int retries;
 *   }
 */
public final class Forms<T> {

    private final Class<T> type;
    private final Map<String, Field<T>> fields = new LinkedHashMap<>();

    private Forms(Class<T> type) {
        this.type = type;

        // inspect bean once
        for (Property p : BeanProperties.of(type)) {
            fields.put(p.name, new Field<>(p));
        }
    }

    /** Entry point, like Grids.of(...) */
    public static <T> Forms<T> of(Class<T> type) {
        return new Forms<>(type);
    }

    /** Minimal auto form: component + BeanValidation where possible. */
    public static <T> DefaultForm<T> auto(Class<T> type) {
        Forms<T> form = new Forms<>(type);

        for (Field<T> field : form.fields.values()) {
            if (field.component == null) {
                field.component(autoComponentFor(field.property));
            }
        }

        return form.build();
    }

    /** Access a form field by property name. */
    public Field<T> field(String name) {
        Field<T> f = fields.get(name);
        if (f == null) {
            throw new IllegalArgumentException(
                    "No such property '" + name + "' on " + type.getSimpleName()
            );
        }
        return f;
    }

    /** Convenience overload to configure a field inline. */
    public Forms<T> field(String name, Consumer<Field<T>> spec) {
        spec.accept(field(name));
        return this;
    }

    /**
     * Attaches an i18n prefix to this form for concise lookups.
     *
     * <pre>{@code
     * Forms.of(UserConfig.class)
     *      .withI18n(i18n, "user.form")
     *      .field("name", f -> {
     *          f.labelI18n("field.label");
     *          f.helpI18n("field.help");
     *      })
     *      .build();
     * }</pre>
     */
    public I18nForm<T> withI18n(I18n i18n, String prefix) {
        return new I18nForm<>(this, i18n, prefix);
    }

    /** Builds the Vaadin form layout. */
    public DefaultForm<T> build() {
        return new DefaultForm<>(type, fields.values());
    }

    // =====================================================================
    // === Field definition (analogous to Grid.Column) ======================
    // =====================================================================

    public static final class Field<T> {

        private final Property property;
        private Component component;
        private final List<Consumer<Binder.BindingBuilder<T, Object>>> validators = new ArrayList<>();

        Field(Property property) {
            this.property = property;
        }

        /** Assigns the Vaadin component used for this field. */
        public Field<T> component(Component component) {
            this.component = component;
            return this;
        }

        /** Sets label if the component supports it. */
        public Field<T> label(String label) {
            if (component instanceof HasLabel hl) {
                hl.setLabel(label);
            }
            return this;
        }

        /** Sets helper text if the component supports it. */
        public Field<T> help(String help) {
            if (component == null) {
                return this;
            }
            try {
                Method m = component.getClass().getMethod("setHelperText", String.class);
                m.invoke(component, help);
            } catch (NoSuchMethodException ignored) {
                // helper text not supported by this component
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        /** Adds validation configuration on the underlying BindingBuilder. */
        public Field<T> validate(Consumer<Binder.BindingBuilder<T, Object>> validator) {
            validators.add(validator);
            return this;
        }

        void bind(Binder<T> binder, Class<T> beanType) {
            if (!(component instanceof HasValue<?, ?>)) {
                throw new IllegalStateException(
                        "Component for property '" + property.name + "' is not a HasValue"
                );
            }

            @SuppressWarnings("unchecked")
            HasValue<?, Object> value = (HasValue<?, Object>) component;

            Binder.BindingBuilder<T, Object> bb = binder.forField(value);

            bb = bb.withValidator(new BeanValidator(beanType, property.name));

            for (Consumer<Binder.BindingBuilder<T, Object>> v : validators) {
                v.accept(bb);
            }

            bb.bind(
                    bean -> property.get(bean),
                    (bean, v) -> property.set(bean, v)
            );
        }


        Component component() {
            return component;
        }
    }

    // =====================================================================
    // === I18n helpers ======================================================
    // =====================================================================

    public static final class I18nForm<T> {

        private final Forms<T> form;
        private final I18n i18n;
        private final String prefix;

        private I18nForm(Forms<T> form, I18n i18n, String prefix) {
            this.form = form;
            this.i18n = i18n;
            this.prefix = prefix;
        }

        public I18nForm<T> field(String name, Consumer<I18nField<T>> spec) {
            Field<T> field = form.field(name);
            spec.accept(new I18nField<>(field, i18n, prefix + "." + name));
            return this;
        }

        public DefaultForm<T> build() {
            return form.build();
        }
    }

    public static final class I18nField<T> {

        private final Field<T> field;
        private final I18n i18n;
        private final String prefix;

        private I18nField(Field<T> field, I18n i18n, String prefix) {
            this.field = field;
            this.i18n = i18n;
            this.prefix = prefix;
        }

        public I18nField<T> component(Component component) {
            field.component(component);
            return this;
        }

        public I18nField<T> labelI18n(String suffix) {
            field.label(i18n.text(prefix + "." + suffix));
            return this;
        }

        public I18nField<T> helpI18n(String suffix) {
            field.help(i18n.text(prefix + "." + suffix));
            return this;
        }

        public I18nField<T> validate(Consumer<Binder.BindingBuilder<T, Object>> validator) {
            field.validate(validator);
            return this;
        }
    }

    // =====================================================================
    // === DefaultForm (result, like Grid.build()) ==========================
    // =====================================================================

    public static final class DefaultForm<T> extends FormLayout {

        private final Binder<T> binder;
        private final Class<T> type;

        DefaultForm(Class<T> type, Collection<Field<T>> fields) {
            this.type = type;
            this.binder = new Binder<>(type);
            binder.setBean(null);

            for (Field<T> f : fields) {
                if (f.component == null) {
                    continue; // allow unused properties
                }
                f.bind(binder, type);
                add(f.component());
            }

        }

        public Binder<T> binder() {
            return binder;
        }

        /** Validates current field values. */
        public BinderValidationStatus<T> validate() {
            return binder.validate();
        }

        /** Convenience validity check. */
        public boolean isValid() {
            return binder.validate().isOk();
        }

        /** Writes form values into a new bean instance. */
        public T getValue() {
            try {
                T bean = type.getDeclaredConstructor().newInstance();
                binder.writeBean(bean);
                return bean;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // =====================================================================
    // === Minimal Bean inspection (internal) ===============================
    // =====================================================================

    static final class Property {

        final String name;
        final Method getter;
        final Method setter;
        final Class<?> valueType;

        Property(String name, Method getter, Method setter) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
            this.valueType = getter.getReturnType();
        }

        Object get(Object bean) {
            try {
                return getter.invoke(bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        void set(Object bean, Object value) {
            try {
                setter.invoke(bean, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    static final class BeanProperties {

        static List<Property> of(Class<?> type) {
            try {
                List<Property> props = new ArrayList<>();
                for (PropertyDescriptor pd :
                        Introspector.getBeanInfo(type).getPropertyDescriptors()) {

                    if (pd.getReadMethod() == null ||
                            pd.getWriteMethod() == null ||
                            "class".equals(pd.getName())) {
                        continue;
                    }

                    props.add(new Property(
                            pd.getName(),
                            pd.getReadMethod(),
                            pd.getWriteMethod()
                    ));
                }
                return props;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Component autoComponentFor(Property property) {
        Class<?> type = property.valueType;

        if (type == String.class) {
            return new TextField();
        }
        if (type == boolean.class || type == Boolean.class) {
            return new Checkbox();
        }
        if (type.isEnum()) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Select select = new Select();
            select.setItems(type.getEnumConstants());
            return select;
        }

        return null;
    }

}
