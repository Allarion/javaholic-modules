package de.javaholic.toolkit.ui.action;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.Binder;
import de.javaholic.toolkit.ui.state.BooleanStates;
import de.javaholic.toolkit.ui.state.DerivedState;
import de.javaholic.toolkit.ui.state.ObservableValue;
import de.javaholic.toolkit.ui.state.Trigger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Action factory entry points.
 *
 * <pre>{@code
 * Action save = Actions.create().label("save").onClick(this::save).build();
 * Action cancel = Actions.noop("cancel");
 * }</pre>
 */
public final class Actions {

    private Actions() {
    }

    /**
     * Creates a fluent builder for {@link Action}.
     */
    public static ActionBuilder create() {
        return new ActionBuilder();
    }

    /**
     * Fluent builder for immutable {@link Action} definitions.
     *
     * <p>Reactive Lite internals stay hidden behind this DSL. You can express
     * conditions directly; no manual {@code MutableState}, {@code Trigger} or
     * {@code DerivedState} wiring is required in user code.</p>
     *
     * <p>Multiple enablement/visibility calls are composed with logical AND.</p>
     *
     * <pre>{@code
     * Action save = Actions.create()
     *     .label("Save")
     *     .enabledWhen(binder)
     *     .disabledWhen(busyCheckbox::getValue)
     *     .onClick(this::save)
     *     .build();
     *
     * Action delete = Actions.create()
     *     .label("Delete")
     *     .visibleWhen(adminCheckbox::getValue)
     *     .hiddenWhen(readOnlyMode::get)
     *     .onClick(this::delete)
     *     .build();
     * }</pre>
     *
     * <p>No background thread support is provided; evaluate and mutate state in
     * Vaadin UI thread only.</p>
     */
    public static final class ActionBuilder {
        private String label;
        private String tooltip;
        private Supplier<Component> iconSupplier;
        private Runnable onClick;
        private ObservableValue<Boolean> enabled = BooleanStates.constant(true);
        private ObservableValue<Boolean> visible = BooleanStates.constant(true);
        private String permission;

        ActionBuilder() {
        }

        public ActionBuilder label(String labelKeyOrText) {
            this.label = labelKeyOrText;
            return this;
        }

        public ActionBuilder tooltip(String tooltipKeyOrText) {
            this.tooltip = tooltipKeyOrText;
            return this;
        }

        public ActionBuilder icon(Component iconComponent) {
            Component icon = Objects.requireNonNull(iconComponent, "iconComponent");
            this.iconSupplier = () -> icon;
            return this;
        }

        public ActionBuilder icon(VaadinIcon icon) {
            VaadinIcon value = Objects.requireNonNull(icon, "icon");
            this.iconSupplier = () -> new Icon(value);
            return this;
        }

        public ActionBuilder onClick(Runnable onClick) {
            this.onClick = onClick;
            return this;
        }

        public ActionBuilder enabledBy(ObservableValue<Boolean> enabled) {
            this.enabled = combineAnd(this.enabled, Objects.requireNonNull(enabled, "enabled"));
            return this;
        }

        public ActionBuilder visibleBy(ObservableValue<Boolean> visible) {
            this.visible = combineAnd(this.visible, Objects.requireNonNull(visible, "visible"));
            return this;
        }

        /**
         * Enables the action when condition is true.
         *
         * <p>The condition is evaluated once during build and then only on explicit
         * trigger events managed by this builder.</p>
         */
        public ActionBuilder enabledWhen(BooleanSupplier condition) {
            return enabledBy(wrap(condition));
        }

        /**
         * Enables the action while the binder is valid.
         *
         * <p>Uses {@link Binder#isValid()} and status change events. No
         * {@code binder.validate()} loop is created.</p>
         */
        public ActionBuilder enabledWhen(Binder<?> binder) {
            Binder<?> source = Objects.requireNonNull(binder, "binder");
            Trigger trigger = new Trigger();
            source.addStatusChangeListener(event -> trigger.fire());
            return enabledBy(DerivedState.of(source::isValid, trigger));
        }

        /**
         * Enables the action when the field value equals {@code Boolean.TRUE}.
         */
        public ActionBuilder enabledWhen(HasValue<?, ?> field) {
            return enabledBy(fromField(field));
        }

        /**
         * Shows the action when condition is true.
         */
        public ActionBuilder visibleWhen(BooleanSupplier condition) {
            return visibleBy(wrap(condition));
        }

        /**
         * Shows the action when the field value equals {@code Boolean.TRUE}.
         */
        public ActionBuilder visibleWhen(HasValue<?, ?> field) {
            return visibleBy(fromField(field));
        }

        /**
         * Disables the action when condition is true.
         */
        public ActionBuilder disabledWhen(BooleanSupplier condition) {
            return enabledBy(BooleanStates.not(wrap(condition)));
        }

        /**
         * Disables the action when the field value equals {@code Boolean.TRUE}.
         */
        public ActionBuilder disabledWhen(HasValue<?, ?> field) {
            return enabledBy(BooleanStates.not(fromField(field)));
        }

        /**
         * Hides the action when condition is true.
         */
        public ActionBuilder hiddenWhen(BooleanSupplier condition) {
            return visibleBy(BooleanStates.not(wrap(condition)));
        }

        /**
         * Hides the action when the field value equals {@code Boolean.TRUE}.
         */
        public ActionBuilder hiddenWhen(HasValue<?, ?> field) {
            return visibleBy(BooleanStates.not(fromField(field)));
        }

        /**
         * Sets optional permission key for UX-level enablement filtering.
         */
        public ActionBuilder permission(String permissionKey) {
            this.permission = permissionKey;
            return this;
        }

        public Action build() {
            // Force one deterministic initial evaluation at build time.
            enabled.get();
            visible.get();
            return new Action(
                    Objects.requireNonNull(label, "label"),
                    tooltip,
                    iconSupplier,
                    onClick != null ? onClick : () -> {
                    },
                    enabled,
                    visible,
                    permission
            );
        }

        private static ObservableValue<Boolean> wrap(BooleanSupplier condition) {
            BooleanSupplier source = Objects.requireNonNull(condition, "condition");
            Trigger trigger = new Trigger();
            return DerivedState.of(source::getAsBoolean, trigger);
        }

        private static ObservableValue<Boolean> fromField(HasValue<?, ?> field) {
            HasValue<?, ?> source = Objects.requireNonNull(field, "field");
            Trigger trigger = new Trigger();
            source.addValueChangeListener(event -> trigger.fire());
            return DerivedState.of(() -> Boolean.TRUE.equals(source.getValue()), trigger);
        }

        private static ObservableValue<Boolean> combineAnd(
                ObservableValue<Boolean> current,
                ObservableValue<Boolean> next
        ) {
            return BooleanStates.and(current, next);
        }
    }
    // ------------------------

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
    public static final class Action {

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

        public String label() {
            return labelKeyOrText;
        }

        public String tooltip() {
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

        /**
         * Optional permission key used for UX-level enablement decisions.
         */
        public Optional<String> permissionKey() {
            return Optional.ofNullable(permissionKey);
        }
    }
}
