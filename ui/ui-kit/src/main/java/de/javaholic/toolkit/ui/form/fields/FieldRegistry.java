package de.javaholic.toolkit.ui.form.fields;

import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
/**
 * TODO (Architecture – Phase 2/3): Revisit FieldRegistry/Factory/Context concept.
 *
 * FieldRegistry currently resolves Vaadin components primarily based on
 * technical property information (type, mappings).
 *
 * Future direction:
 * - FieldRegistry should operate purely on UiProperty / UI semantic context.
 * - UI annotations (@UiHidden, @UiLabel, @UiOrder, etc.) must be evaluated
 *   in UiMeta, not here.
 * - This class should not perform BeanMeta introspection or visibility decisions.
 *
 * Long-term goal:
 * FieldRegistry becomes a UI component resolution strategy,
 * while UiMeta remains the single source of truth for UI semantics.
 */

/**
 * Registry that maps property/type context to Vaadin field factories.
 *
 * <p>Responsibility: resolve a {@link FieldFactory} from property overrides, type overrides,
 * config aliases, and defaults, then create a {@link HasValue} component.</p>
 *
 * <p>Must not do: inspect beans, decide visibility/order, or perform binding logic.</p>
 *
 * <p>Architecture fit: pluggable field-instantiation backend used by {@code Forms}.</p>
 *
 *
 *
 * <p>Usage:</p>
 * <pre>{@code
 * FieldRegistry registry = new FieldRegistry();
 * registry.override(String.class, ctx -> Inputs.textField().build());
 * HasValue<?, ?> field = registry.create(new FieldContext(User.class, "email", String.class, User.class.getDeclaredField("email")));
 * }</pre>
 */
public final class FieldRegistry {
    // ------------------------------------------------------------------
    // UI semantic annotation evaluation happens *only* in UiMeta.
    // Label resolution to actual display text happens *only* via TextResolver.
    // FieldRegistry consumes resolved attributes, but never evaluates annotations.
    // ------------------------------------------------------------------

    private final Map<Class<?>, FieldFactory> defaultByType;
    private final Map<String, FieldFactory> aliasFactories;
    private final Map<String, String> configMappings;

    private final Map<Class<?>, FieldFactory> typeOverrides = new ConcurrentHashMap<>();
    private final Map<PropertyKey, FieldFactory> propertyOverrides = new ConcurrentHashMap<>();

    /**
     * Creates a registry with built-in default mappings.
     *  // TODO: check: should this be availble as framework bean?
     * <p>Example: {@code FieldRegistry registry = new FieldRegistry();}</p>
     */
    public FieldRegistry() {
        this(DefaultMappings.defaults(), Map.of());
    }

    /**
     * Creates a registry with custom defaults and alias/config mappings.
     *
     * <p>Example: {@code new FieldRegistry(DefaultMappings.defaults(), Map.of("Enum", "select"));}</p>
     */
    public FieldRegistry(DefaultMappings defaults, Map<String, String> configMappings) {
        Objects.requireNonNull(defaults, "defaults");
        this.defaultByType = defaults.byType();
        this.aliasFactories = normalizeAliases(defaults.aliases());
        this.configMappings = Collections.unmodifiableMap(new LinkedHashMap<>(configMappings));
    }

    /**
     * Creates a field component for the provided property context.
     *
     * <p>Example: {@code HasValue<?, ?> field = registry.create(ctx);}</p>
     */
    public HasValue<?, ?> create(FieldContext ctx) {
        return create(ctx, null, false);
    }

    /**
     * Creates a field component for the provided property context and consumes semantic hints.
     *
     * <p>Example: {@code HasValue<?, ?> field = registry.create(ctx, "user.email.label", true);}</p>
     */
    public HasValue<?, ?> create(FieldContext ctx, String labelKey, boolean readOnly) {
        Objects.requireNonNull(ctx, "ctx");

        FieldFactory factory = propertyOverrides.get(PropertyKey.of(ctx.declaringType(), ctx.property()));
        if (factory != null) {
            return applySemanticHints(requireFactoryResult(factory, ctx), labelKey, readOnly);
        }

        factory = resolveByType(typeOverrides, ctx.fieldType());
        if (factory != null) {
            return applySemanticHints(requireFactoryResult(factory, ctx), labelKey, readOnly);
        }

        factory = resolveByConfig(ctx);
        if (factory != null) {
            return applySemanticHints(requireFactoryResult(factory, ctx), labelKey, readOnly);
        }

        factory = resolveByType(defaultByType, ctx.fieldType());
        if (factory != null) {
            return applySemanticHints(requireFactoryResult(factory, ctx), labelKey, readOnly);
        }

        throw new IllegalStateException(
                "No FieldFactory registered for " +
                        ctx.declaringType().getSimpleName() +
                        "#" + ctx.property() +
                        " (" + ctx.fieldType().getName() + ")"
        );
    }

    /**
     * Overrides field creation for all properties of a Java type.
     *
     * <p>Example: {@code registry.override(UUID.class, ctx -> Inputs.uuidField().build());}</p>
     */
    public void override(Class<?> type, FieldFactory factory) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(factory, "factory");
        typeOverrides.put(type, factory);
    }

    /**
     * Overrides field creation for one specific DTO property.
     *
     * <p>Example: {@code registry.override(User.class, "email", ctx -> Inputs.emailField().build());}</p>
     */
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

    private static HasValue<?, ?> requireFactoryResult(FieldFactory factory, FieldContext ctx) {
        HasValue<?, ?> value = factory.create(ctx);
        if (value == null) {
            throw new IllegalStateException(
                    "FieldFactory returned null for " +
                            ctx.declaringType().getSimpleName() +
                            "#" + ctx.property()
            );
        }
        return value;
    }

    private static HasValue<?, ?> applySemanticHints(HasValue<?, ?> value, String labelKey, boolean readOnly) {
        value.setReadOnly(readOnly);
        if (labelKey != null && !labelKey.isBlank() && value instanceof HasLabel hasLabel) {
            hasLabel.setLabel(labelKey);
        }
        return value;
    }

    private record PropertyKey(Class<?> dto, String property) {
        private static PropertyKey of(Class<?> dto, String property) {
            return new PropertyKey(dto, property);
        }
    }
}
