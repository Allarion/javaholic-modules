package de.javaholic.util.ui.vaadin.fluent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Fluent factories for Vaadin input components.
 *
 * Usage:
 *   TextField name =
 *     Inputs.text()
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

    // ---------- Fluent Builder ----------

    public static final class InputBuilder<T extends Component> {

        private final T component;

        private InputBuilder(T component) {
            this.component = component;
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
            return component;
        }
    }
}
