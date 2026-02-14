package de.javaholic.toolkit.ui.form.fields;

import com.vaadin.flow.component.HasValue;
import de.javaholic.toolkit.ui.Inputs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

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
        byType.put(String.class, ctx -> Inputs.textField().build());
        byType.put(Boolean.class, ctx -> Inputs.checkbox().build());
        byType.put(boolean.class, ctx -> Inputs.checkbox().build());
        byType.put(Integer.class, ctx -> Inputs.numberField().build());
        byType.put(int.class, ctx -> Inputs.numberField().build());
        byType.put(Long.class, ctx -> Inputs.numberField().build());
        byType.put(long.class, ctx -> Inputs.numberField().build());
        byType.put(BigDecimal.class, ctx -> Inputs.numberField().build());
        byType.put(LocalDate.class, ctx -> Inputs.datePicker().build());
        byType.put(UUID.class, ctx -> Inputs.uuidField().build());
        byType.put(Enum.class, DefaultMappings::enumSelect);

        Map<String, FieldFactory> aliases = new LinkedHashMap<>();
        aliases.put("text", ctx -> Inputs.textField().build());
        aliases.put("textarea", ctx -> Inputs.textArea().build());
        aliases.put("email", ctx -> Inputs.emailField().build());
        aliases.put("checkbox", ctx -> Inputs.checkbox().build());
        aliases.put("number", ctx -> Inputs.numberField().build());
        aliases.put("date", ctx -> Inputs.datePicker().build());
        aliases.put("uuid", ctx -> Inputs.uuidField().build());
        aliases.put("enum", DefaultMappings::enumSelect);
        aliases.put("combo", DefaultMappings::enumSelect);

        return new DefaultMappings(byType, aliases);
    }

    public Map<Class<?>, FieldFactory> byType() {
        return byType;
    }

    public Map<String, FieldFactory> aliases() {
        return aliases;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> HasValue<?, ?> enumSelect(FieldContext ctx) {
        if (!ctx.fieldType().isEnum()) {
            return Inputs.textField().build();
        }
        Class<E> enumType = (Class<E>) ctx.fieldType();
        return Inputs.select(enumType).build();
    }
}
