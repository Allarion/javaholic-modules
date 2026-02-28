package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import de.javaholic.toolkit.iam.core.api.PermissionChecker;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.ui.component.UUIDField;
import de.javaholic.toolkit.i18n.DefaultTextResolver;
import de.javaholic.toolkit.ui.policy.DefaultUiPolicyEngine;
import de.javaholic.toolkit.ui.policy.UiPolicyContext;


/**
 * Fluent factories for Vaadin input components.
 * <p>
 * Usage:
 * TextField name =
 * Inputs.textField()
 * .label("user.name")
 * .placeholder("user.name.placeholder")
 * .widthFull()
 * .withClassName("config-field")
 * .build();
 * <p>
 * You can always skip this and use Vaadin directly.
 */
public final class Inputs {
    private static final DefaultUiPolicyEngine POLICY_ENGINE = new DefaultUiPolicyEngine();

    // TODO: add TESTS

    private Inputs() {
    }
    // ---------- Factories ----------

    public static InputBuilder<TextField> textField() {
        return new InputBuilder<>(new TextField());
    }

    public static InputBuilder<TextArea> textArea() {
        return new InputBuilder<>(new TextArea());
    }

    public static InputBuilder<EmailField> emailField() {
        return new InputBuilder<>(new EmailField());
    }

    public static InputBuilder<NumberField> numberField() {
        return new InputBuilder<>(new NumberField());
    }

    public static InputBuilder<Checkbox> checkbox() {
        return new InputBuilder<>(new Checkbox());
    }

    public static InputBuilder<DatePicker> datePicker() {
        return new InputBuilder<>(new DatePicker());
    }

    public static InputBuilder<UUIDField> uuidField() {
        return new InputBuilder<>(new UUIDField());
    }

    public static <E extends Enum<E>>
    InputBuilder<Select<E>> select(Class<E> enumType) {
        Select<E> select = new Select<>();
        select.setItems(enumType.getEnumConstants());
        return new InputBuilder<>(select);
    }

    public static <E extends Enum<E>>
    InputBuilder<MultiSelectComboBox<E>> multiSelect(Class<E> enumType) {
        MultiSelectComboBox<E> box = new MultiSelectComboBox<>();
        box.setItems(enumType.getEnumConstants());
        return new InputBuilder<>(box);
    }

    public static <T>
    InputBuilder<MultiSelectComboBox<T>> multiselect(Class<T> type) {
        return new InputBuilder<>(new MultiSelectComboBox<>());
    }

    // ---------- Fluent Builder ----------

    public static final class InputBuilder<T extends Component> {

        private final T component;
        // UI boundary: keys remain semantic until component build/apply time.
        private TextResolver textResolver = new DefaultTextResolver();
        private String labelKey;
        private String descriptionKey;
        private String tooltipKey;
        private String errorKey;
        private String placeholderKey;
        private PermissionChecker permissionChecker;
        private String permissionKey;

        private InputBuilder(T component) {
            this.component = component;
        }

        public InputBuilder<T> withTextResolver(TextResolver textResolver) {
            this.textResolver = java.util.Objects.requireNonNull(textResolver, "textResolver");
            return this;
        }

        public InputBuilder<T> withPermissionChecker(PermissionChecker permissionChecker) {
            this.permissionChecker = permissionChecker;
            return this;
        }

        public InputBuilder<T> permission(String permissionKey) {
            this.permissionKey = permissionKey;
            return this;
        }

        public InputBuilder<T> label(String key) {
            this.labelKey = key;
            return this;
        }

        public InputBuilder<T> description(String key) {
            this.descriptionKey = key;
            return this;
        }

        public InputBuilder<T> tooltip(String key) {
            this.tooltipKey = key;
            return this;
        }

        public InputBuilder<T> error(String key) {
            this.errorKey = key;
            return this;
        }

        public InputBuilder<T> placeholder(String key) {
            this.placeholderKey = key;
            return this;
        }

        /**
         * Sets width to 100% if the component supports sizing.
         */
        public InputBuilder<T> widthFull() {
            if (component instanceof HasSize hs) {
                hs.setWidthFull();
            }
            return this;
        }

        /**
         * Adds a CSS class name.
         */
        public InputBuilder<T> withClassName(String className) {
            component.addClassName(className);
            return this;
        }

        /**
         * Adds a theme name to the element.
         */
        public InputBuilder<T> withTheme(String theme) {
            component.getElement().getThemeList().add(theme);
            return this;
        }

        /**
         * Returns the underlying Vaadin component.
         */
        public T build() {
            applyTexts();
            applyPolicy();
            return component;
        }

        private void applyTexts() {
            if (labelKey != null && component instanceof HasLabel hasLabel) {
                hasLabel.setLabel(textResolver.resolve(labelKey).orElse(labelKey));
            }

            if (descriptionKey != null && component instanceof HasHelper hasHelper) {
                hasHelper.setHelperText(textResolver.resolve(descriptionKey).orElse(descriptionKey));
            }

            if (tooltipKey != null) {
                // component.setTooltipText(resolve(tooltipKey));
                component.getElement()
                        .setProperty("title", textResolver.resolve(tooltipKey).orElse(tooltipKey));
            }

            if (errorKey != null && component instanceof HasValidation hasValidation) {
                hasValidation.setErrorMessage(textResolver.resolve(errorKey).orElse(errorKey));
            }

            if (placeholderKey != null && component instanceof HasPlaceholder hasPlaceholder) {
                hasPlaceholder.setPlaceholder(textResolver.resolve(placeholderKey).orElse(placeholderKey));
            }
        }

        // TODO: why not VaadinActionBinder? also all Inputs are HasEnabled.
        private void applyPolicy() {
            boolean allowed = POLICY_ENGINE.isPermissionAllowed(
                    permissionKey,
                    new UiPolicyContext(permissionChecker, null)
            );
            component.setVisible(allowed);
            if (component instanceof HasEnabled hasEnabled) {
                hasEnabled.setEnabled(allowed);
            }
            if (component instanceof HasValue<?, ?> hasValue) {
                hasValue.setReadOnly(!allowed);
            }
        }
    }
}
