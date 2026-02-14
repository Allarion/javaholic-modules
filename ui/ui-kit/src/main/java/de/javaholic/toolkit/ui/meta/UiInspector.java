package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanIntrospector;
import de.javaholic.toolkit.introspection.BeanMeta;

import java.util.Objects;

/**
 * Factory for UI semantic metadata.
 *
 * <p>Responsibilities:</p>
 * <ul>
 * <li>Wrap technical {@link BeanMeta} into UI-facing {@link UiMeta}</li>
 * <li>Keep UI semantics out of {@code BeanMeta} (architecture no-go rule)</li>
 * </ul>
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
