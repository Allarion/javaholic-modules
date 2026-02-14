package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Central UI semantic model for a bean type.
 *
 * <p>Responsibilities:</p>
 * <ul>
 * <li>Single source of truth for default UI visibility decisions</li>
 * <li>Provide {@link UiProperty} stream consumed by Grid/Form auto builders</li>
 * </ul>
 *
 * <p>No-go rules enforced by design:</p>
 * <ul>
 * <li>{@link BeanMeta} remains technical; UI semantics are not added there</li>
 * <li>Grid/Form/Crud should not duplicate hidden/id/version logic</li>
 * </ul>
 *
 * <p>Phase 1 behavior:</p>
 * <ul>
 * <li>ID property hidden by default</li>
 * <li>Version property hidden by default</li>
 * <li>All others visible by default</li>
 * </ul>
 *
 * <p>Example:</p>
 * <pre>{@code
 * UiMeta<User> meta = UiInspector.inspect(User.class);
 * meta.properties().forEach(property -> {
 *     if (property.isVisible()) {
 *         System.out.println(property.label());
 *     }
 * });
 * }</pre>
 */
public final class UiMeta<T> {

    private final BeanMeta<T> beanMeta;

    UiMeta(BeanMeta<T> beanMeta) {
        this.beanMeta = Objects.requireNonNull(beanMeta, "beanMeta");
    }

    public Class<T> type() {
        return beanMeta.type();
    }

    public BeanMeta<T> beanMeta() {
        return beanMeta;
    }

    public Stream<UiProperty<T>> properties() {
        Set<String> hiddenProperties = hiddenPropertyNames();
        return beanMeta.properties()
                .stream()
                // TODO phase 2: resolve visibility/label/order defaults from UI annotations.
                .map(property -> new UiProperty<>(
                        beanMeta,
                        property,
                        !hiddenProperties.contains(property.name()),
                        property.name(),
                        Integer.MAX_VALUE
                ));
    }

    private Set<String> hiddenPropertyNames() {
        return Stream.concat(
                        beanMeta.idProperty().stream(),
                        beanMeta.versionProperty().stream()
                )
                .map(BeanProperty::name)
                .collect(Collectors.toUnmodifiableSet());
    }
}
