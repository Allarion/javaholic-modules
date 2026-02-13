package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import de.javaholic.toolkit.i18n.I18n;
import de.javaholic.toolkit.i18n.Text;
import de.javaholic.toolkit.i18n.Texts;


/**
 * Fluent factories for Vaadin input components.
 *
 * Usage:
 *   TextField name =
 *     Inputs.text()
 *           .withI18n(i18n)
 *           .text(Texts.label("user.name"))
 *           .widthFull()
 *           .withClassName("config-field")
 *           .build();
 *
 * You can always skip this and use Vaadin directly.
 */
public final class Inputs {

    private Inputs() {}

    // ---------- Factories ----------

    public static InputBuilder<TextField> text() {
        return new InputBuilder<>(new TextField());
    }

    public static InputBuilder<NumberField> number() {
        return new InputBuilder<>(new NumberField());
    }

    public static InputBuilder<Checkbox> checkbox() {
        return new InputBuilder<>(new Checkbox());
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
        private I18n i18n;
        private Text labelText;
        private Text descriptionText;
        private Text tooltipText;
        private Text errorText;

        private InputBuilder(T component) {
            this.component = component;
        }

        /** Sets the i18n instance used by {@link #text(Text...)}. */
        public InputBuilder<T> withI18n(I18n i18n) {
            this.i18n = i18n;
            return this;
        }

        /**
         * Sets input texts using the Text model.
         *
         * <p>Supported roles: LABEL, DESCRIPTION, TOOLTIP, ERROR.</p>
         */
        public InputBuilder<T> text(Text... texts) {
            if (texts == null) {
                return this;
            }
            for (Text text : texts) {
                if (text == null) {
                    continue;
                }
                switch (text.role()) {
                    case LABEL -> this.labelText = text;
                    case DESCRIPTION -> this.descriptionText = text;
                    case TOOLTIP -> this.tooltipText = text;
                    case ERROR -> this.errorText = text;
                    default -> {
                        // ignore unsupported roles
                    }
                }
            }
            return this;
        }

        /** Sets width to 100% if the component supports sizing. */
        public InputBuilder<T> widthFull() {
            if (component instanceof HasSize hs) {
                hs.setWidthFull();
            }
            return this;
        }

        /** Adds a CSS class name. */
        public InputBuilder<T> withClassName(String className) {
            component.addClassName(className);
            return this;
        }

        /** Adds a theme name to the element. */
        public InputBuilder<T> withTheme(String theme) {
            component.getElement().getThemeList().add(theme);
            return this;
        }

        /** Returns the underlying Vaadin component. */
        public T build() {
            applyTexts();
            return component;
        }

        private void applyTexts() {
            if (labelText != null && component instanceof HasLabel hasLabel) {
                hasLabel.setLabel(Texts.resolve(i18n, labelText));
            }

            if (descriptionText != null && component instanceof HasHelper hasHelper) {
                hasHelper.setHelperText(Texts.resolve(i18n, descriptionText));
            }

            if (tooltipText != null) {
                // component.setTooltipText(Texts.resolve(i18n, tooltipText));
                component.getElement()
                        .setProperty("title", Texts.resolve(i18n, tooltipText));
            }

            if (errorText != null && component instanceof HasValidation hasValidation) {
                hasValidation.setErrorMessage(Texts.resolve(i18n, errorText));
            }
        }

    }
}
