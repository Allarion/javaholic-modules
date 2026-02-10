package de.javaholic.toolkit.ui.form.fields;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DefaultMappings {

    private final Map<Class<?>, FieldFactory> byType;
    private final Map<String, FieldFactory> aliases;

    private DefaultMappings(
            Map<Class<?>, FieldFactory> byType,
            Map<String, FieldFactory> aliases
    ) {
        this.byType = Collections.unmodifiableMap(byType);
        this.aliases = Collections.unmodifiableMap(aliases);
    }

    public static DefaultMappings defaults() {
        Map<Class<?>, FieldFactory> byType = new LinkedHashMap<>();
        byType.put(String.class, ctx -> new TextField());
        byType.put(Boolean.class, ctx -> new Checkbox());
        byType.put(boolean.class, ctx -> new Checkbox());
        byType.put(Integer.class, ctx -> new NumberField());
        byType.put(int.class, ctx -> new NumberField());
        byType.put(Long.class, ctx -> new NumberField());
        byType.put(long.class, ctx -> new NumberField());
        byType.put(BigDecimal.class, ctx -> new NumberField());
        byType.put(LocalDate.class, ctx -> new DatePicker());
        byType.put(Enum.class, DefaultMappings::enumCombo);

        Map<String, FieldFactory> aliases = new LinkedHashMap<>();
        aliases.put("text", ctx -> new TextField());
        aliases.put("textarea", ctx -> new TextArea());
        aliases.put("email", ctx -> new EmailField());
        aliases.put("checkbox", ctx -> new Checkbox());
        aliases.put("number", ctx -> new NumberField());
        aliases.put("date", ctx -> new DatePicker());
        aliases.put("enum", DefaultMappings::enumCombo);
        aliases.put("combo", DefaultMappings::enumCombo);

        return new DefaultMappings(byType, aliases);
    }

    public Map<Class<?>, FieldFactory> byType() {
        return byType;
    }

    public Map<String, FieldFactory> aliases() {
        return aliases;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> ComboBox<E> enumCombo(FieldContext ctx) {
        ComboBox<E> box = new ComboBox<>();
        if (!ctx.fieldType().isEnum()) {
            return box;
        }
        Class<E> enumType = (Class<E>) ctx.fieldType();
        box.setItems(enumType.getEnumConstants());
        return box;
    }
}
