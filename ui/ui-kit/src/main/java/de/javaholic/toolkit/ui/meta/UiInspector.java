package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanIntrospector;
import de.javaholic.toolkit.introspection.BeanMeta;

import java.util.Objects;

/**
 * Factory for UI semantic metadata.
 *
 * <p>Responsibility:</p>
 * <ul>
 * <li>Bridge technical introspection to UI metadata by wrapping {@link BeanMeta} into {@link UiMeta}</li>
 * </ul>
 *
 * <p>Must not do: render components, configure Grid/Form fields, or hold CRUD orchestration logic.</p>
 *
 * <p>Architecture fit: single entry point for UI metadata creation. Consumers use this to keep
 * {@code BeanMeta} technical-only and prevent direct reflection coupling in UI builders.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * UiMeta<User> meta = UiInspector.inspect(User.class);
 * meta.properties()
 *     .filter(UiProperty::isVisible)
 *     .forEach(p -> System.out.println(p.name()));
 * }</pre>
 */
public final class UiInspector {

    private UiInspector() {
    }

    public static <T> UiMeta<T> inspect(Class<T> type) {
        Objects.requireNonNull(type, "type");
        return new UiMeta<>(BeanIntrospector.inspect(type));
    }
}
