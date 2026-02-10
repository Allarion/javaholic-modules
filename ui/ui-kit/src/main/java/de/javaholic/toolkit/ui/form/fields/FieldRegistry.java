package de.javaholic.toolkit.ui.form.fields;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class FieldRegistry {

    private final Map<Class<?>, FieldFactory> defaultByType;
    private final Map<String, FieldFactory> aliasFactories;
    private final Map<String, String> configMappings;

    private final Map<Class<?>, FieldFactory> typeOverrides = new ConcurrentHashMap<>();
    private final Map<PropertyKey, FieldFactory> propertyOverrides = new ConcurrentHashMap<>();

    public FieldRegistry() {
        this(DefaultMappings.defaults(), Map.of());
    }

    public FieldRegistry(DefaultMappings defaults, Map<String, String> configMappings) {
        Objects.requireNonNull(defaults, "defaults");
        this.defaultByType = defaults.byType();
        this.aliasFactories = normalizeAliases(defaults.aliases());
        this.configMappings = Collections.unmodifiableMap(new LinkedHashMap<>(configMappings));
    }

    public Component create(FieldContext ctx) {
        Objects.requireNonNull(ctx, "ctx");

        FieldFactory factory = propertyOverrides.get(PropertyKey.of(ctx.declaringType(), ctx.property()));
        if (factory != null) {
            return factory.create(ctx);
        }

        factory = resolveByType(typeOverrides, ctx.fieldType());
        if (factory != null) {
            return factory.create(ctx);
        }

        factory = resolveByConfig(ctx);
        if (factory != null) {
            return factory.create(ctx);
        }

        factory = resolveByType(defaultByType, ctx.fieldType());
        if (factory != null) {
            return factory.create(ctx);
        }

        return new TextField();
    }

    public void override(Class<?> type, FieldFactory factory) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(factory, "factory");
        typeOverrides.put(type, factory);
    }

    public void override(Class<?> dto, String property, FieldFactory factory) {
        Objects.requireNonNull(dto, "dto");
        Objects.requireNonNull(property, "property");
        Objects.requireNonNull(factory, "factory");
        propertyOverrides.put(PropertyKey.of(dto, property), factory);
    }

    private FieldFactory resolveByConfig(FieldContext ctx) {
        String alias = configMappings.get(propertyKey(ctx));
        if (alias == null) {
            alias = configMappings.get(simplePropertyKey(ctx));
        }
        if (alias == null) {
            alias = configMappings.get(ctx.fieldType().getName());
        }
        if (alias == null) {
            alias = configMappings.get(ctx.fieldType().getSimpleName());
        }
        if (alias == null && ctx.fieldType().isEnum()) {
            alias = configMappings.get("Enum");
        }
        if (alias == null && ctx.fieldType().isPrimitive()) {
            alias = configMappings.get(primitiveAlias(ctx.fieldType()));
        }
        if (alias == null) {
            return null;
        }
        return aliasFactories.get(alias.toLowerCase(Locale.ROOT));
    }

    private static FieldFactory resolveByType(
            Map<Class<?>, FieldFactory> mappings,
            Class<?> fieldType
    ) {
        FieldFactory factory = mappings.get(fieldType);
        if (factory != null) {
            return factory;
        }
        if (fieldType.isPrimitive()) {
            factory = mappings.get(primitiveWrapper(fieldType));
            if (factory != null) {
                return factory;
            }
        }
        if (fieldType.isEnum()) {
            factory = mappings.get(Enum.class);
            if (factory != null) {
                return factory;
            }
        }
        for (Map.Entry<Class<?>, FieldFactory> entry : mappings.entrySet()) {
            if (entry.getKey().isAssignableFrom(fieldType)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static String propertyKey(FieldContext ctx) {
        return ctx.declaringType().getName() + "#" + ctx.property();
    }

    private static String simplePropertyKey(FieldContext ctx) {
        return ctx.declaringType().getSimpleName() + "#" + ctx.property();
    }

    private static Class<?> primitiveWrapper(Class<?> primitive) {
        if (primitive == boolean.class) {
            return Boolean.class;
        }
        if (primitive == int.class) {
            return Integer.class;
        }
        if (primitive == long.class) {
            return Long.class;
        }
        return primitive;
    }

    private static String primitiveAlias(Class<?> primitive) {
        if (primitive == boolean.class) {
            return "Boolean";
        }
        if (primitive == int.class) {
            return "Integer";
        }
        if (primitive == long.class) {
            return "Long";
        }
        return primitive.getSimpleName();
    }

    private static Map<String, FieldFactory> normalizeAliases(Map<String, FieldFactory> aliases) {
        Map<String, FieldFactory> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, FieldFactory> entry : aliases.entrySet()) {
            normalized.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
        }
        return Collections.unmodifiableMap(normalized);
    }

    private record PropertyKey(Class<?> dto, String property) {
        private static PropertyKey of(Class<?> dto, String property) {
            return new PropertyKey(dto, property);
        }
    }
}
