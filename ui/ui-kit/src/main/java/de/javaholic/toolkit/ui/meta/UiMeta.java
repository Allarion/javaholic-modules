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
 * <p>Responsibility:</p>
 * <ul>
 * <li>Centralize UI semantics (visibility, label, order) above technical metadata</li>
 * <li>Expose {@link UiProperty} values consumed by auto Grid/Form builders</li>
 * </ul>
 *
 * <p>Must not do:</p>
 * <ul>
 * <li>Must not push UI rules into {@link BeanMeta}; {@code BeanMeta} is technical only</li>
 * <li>Must not render components or bind Vaadin widgets directly</li>
 * </ul>
 *
 * <p>Architecture fit:</p>
 * <ul>
 * <li>Wraps {@link BeanMeta} and is the single source of UI defaults for Phase 1</li>
 * <li>{@code Grids.auto(...)} and {@code Forms.auto(...)} must consume this instead of direct bean introspection</li>
 * </ul>
 *
 * <p>Phase 1 behavior (defaults-only by design):</p>
 * <ul>
 * <li>ID property hidden by default</li>
 * <li>Version property hidden by default</li>
 * <li>All others visible by default</li>
 * </ul>
 *
 * <p>Planned Phase 2 extensions:</p>
 * <ul>
 * <li>Support for {@code @UiHidden}, {@code @UiLabel}, {@code @UiOrder}</li>
 * <li>Optional dynamic DTO proxy support</li>
 * </ul>
 *
 *
 * <p>Example:</p>
 * <pre>{@code
 * UiMeta<User> meta = UiInspector.inspect(User.class);
 * meta.properties()
 *     .filter(UiProperty::isVisible)
 *     .forEach(p -> System.out.println(p.name()));
 * }</pre>
 */
public final class UiMeta<T> {

    private final BeanMeta<T> beanMeta;

    UiMeta(BeanMeta<T> beanMeta) {
        // Architecture boundary: UiMeta wraps technical BeanMeta so UI layers never use BeanMeta directly.
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
        return beanMeta.properties().stream()
                // TODO phase 2: support @UiHidden on property/record component and combine with defaults.
                // TODO phase 2: support @UiLabel, optionally i18n-aware label keys.
                // TODO phase 2: support @UiOrder for stable ordering before builder-level overrides.
                // TODO phase 2: support dynamic DTO proxy types that do not expose concrete fields directly.
                .map(property -> new UiProperty<>(beanMeta, property, !hiddenProperties.contains(property.name()), property.name(), Integer.MAX_VALUE));
    }

    private Set<String> hiddenPropertyNames() {
        return Stream.concat(beanMeta.idProperty().stream(), beanMeta.versionProperty().stream()).map(BeanProperty::name).collect(Collectors.toUnmodifiableSet());
    }
}
