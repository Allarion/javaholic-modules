package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanMeta;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Central UI semantic model for a bean type.
 *
 * <p>Responsibility:</p>
 * <ul>
 * <li>Centralize UI semantics (visibility, labelKey, order, readOnly) above technical metadata</li>
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
 * <li>Wraps {@link BeanMeta} and is the single source of UI semantics</li>
 * <li>{@code Grids.auto(...)} and {@code Forms.auto(...)} must consume this instead of direct bean introspection</li>
 * </ul>
 *
 * <p>Semantic model rules:</p>
 * <ul>
 * <li>Annotation evaluation happens in {@link UiInspector}</li>
 * <li>UiMeta stores label keys, not resolved labels</li>
 * <li>If no annotation overrides visibility, Phase 1 defaults still apply ({@code id}/{@code version} hidden)</li>
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
public final class UiMeta<T> {

    private final BeanMeta<T> beanMeta;
    private final List<UiProperty<T>> properties;

    UiMeta(BeanMeta<T> beanMeta, List<UiProperty<T>> properties) {
        // Architecture boundary: UiMeta wraps technical BeanMeta so UI layers never use BeanMeta directly.
        this.beanMeta = Objects.requireNonNull(beanMeta, "beanMeta");
        this.properties = List.copyOf(Objects.requireNonNull(properties, "properties"));
    }

    /**
     * Returns the inspected model type.
     *
     * <p>Example: {@code Class<User> t = uiMeta.type();}</p>
     */
    public Class<T> type() {
        return beanMeta.type();
    }

    /**
     * Returns the wrapped technical metadata object.
     *
     * <p>Example: {@code BeanMeta<User> technical = uiMeta.beanMeta();}</p>
     */
    public BeanMeta<T> beanMeta() {
        return beanMeta;
    }

    /**
     * Returns UI properties with semantic defaults and annotation overrides applied.
     *
     * <p>Example: {@code uiMeta.properties().filter(UiProperty::isVisible).forEach(p -> {});}</p>
     */
    public Stream<UiProperty<T>> properties() {
        return properties.stream();
    }
}
