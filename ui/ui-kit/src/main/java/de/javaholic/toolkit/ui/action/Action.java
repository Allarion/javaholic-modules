package de.javaholic.toolkit.ui.action;

import com.vaadin.flow.component.Component;
import de.javaholic.toolkit.ui.state.BooleanStates;
import de.javaholic.toolkit.ui.state.ObservableValue;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Immutable UI action definition.
 *
 * <p>An action captures text, tooltip, icon, behavior and reactive visibility/
 * enablement state. Rendering is done by helpers like {@code Buttons.from(...)}
 * or layout builders.</p>
 *
 * <pre>{@code
 * Action save = Actions.create()
 *     .label("save")
 *     .tooltip("save.tooltip")
 *     .enabledBy(formValid)
 *     .onClick(this::save)
 *     .build();
 * }</pre>
 *
 * <p>Threading rule: no background threads; evaluate and update from Vaadin UI
 * thread.</p>
 */
public final class Action {

    private final String labelKeyOrText;
    private final String tooltipKeyOrText;
    private final Supplier<Component> iconSupplier;
    private final Runnable onClick;
    private final ObservableValue<Boolean> enabled;
    private final ObservableValue<Boolean> visible;
    private final String permissionKey;

    Action(
            String labelKeyOrText,
            String tooltipKeyOrText,
            Supplier<Component> iconSupplier,
            Runnable onClick,
            ObservableValue<Boolean> enabled,
            ObservableValue<Boolean> visible,
            String permissionKey
    ) {
        this.labelKeyOrText = Objects.requireNonNull(labelKeyOrText, "labelKeyOrText");
        this.tooltipKeyOrText = tooltipKeyOrText;
        this.iconSupplier = iconSupplier;
        this.onClick = Objects.requireNonNull(onClick, "onClick");
        this.enabled = Objects.requireNonNullElse(enabled, BooleanStates.constant(true));
        this.visible = Objects.requireNonNullElse(visible, BooleanStates.constant(true));
        this.permissionKey = permissionKey;
    }

    // TODO: codesmell!!
    public String labelKeyOrText() {
        return labelKeyOrText;
    }

    // TODO: codesmell!!
    public String tooltipKeyOrText() {
        return tooltipKeyOrText;
    }

    public Optional<Component> createIcon() {
        return iconSupplier != null ? Optional.ofNullable(iconSupplier.get()) : Optional.empty();
    }

    public Runnable onClick() {
        return onClick;
    }

    public ObservableValue<Boolean> enabled() {
        return enabled;
    }

    public ObservableValue<Boolean> visible() {
        return visible;
    }

    public Optional<String> permissionKey() {
        return Optional.ofNullable(permissionKey);
    }
}
