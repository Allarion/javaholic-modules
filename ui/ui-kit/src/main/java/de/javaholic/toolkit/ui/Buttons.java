package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import de.javaholic.toolkit.i18n.DefaultTextResolver;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.ui.action.Action;
import de.javaholic.toolkit.ui.action.Actions;
import de.javaholic.toolkit.ui.action.vaadin.VaadinActionBinder;
import de.javaholic.toolkit.ui.state.DerivedState;
import de.javaholic.toolkit.ui.state.ObservableValue;
import de.javaholic.toolkit.ui.state.Trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Fluent builder for Vaadin {@link Button Buttons}.
 *
 * <p>Reactive Lite mode:
 * no background threads, no polling framework, no hidden async behavior.
 * UI updates are applied in Vaadin UI lifecycle via subscriptions.</p>
 *
 * <pre>{@code
 * Button save = Buttons.create()
 *     .label("save")
 *     .enabledBy(formValid)
 *     .action(this::save)
 *     .build();
 *
 * Button delete = Buttons.from(
 *     Actions.create()
 *         .label("delete")
 *         .visibleBy(isAdmin)
 *         .onClick(this::delete)
 *         .build()
 * );
 * }</pre>
 */
public final class Buttons {

    private Buttons() {
    }

    /**
     * Entry point for fluent button creation.
     */
    public static Builder create() {
        return new Builder();
    }

    /**
     * Renders a Vaadin {@link Button} from immutable {@link Action} definition.
     *
     * <p>Subscriptions are tied to component detach lifecycle.</p>
     */
    public static Button from(Action action) {
        Objects.requireNonNull(action, "action");

        Button button = new Button(action.labelKeyOrText());
        if (action.tooltipKeyOrText() != null) {
            button.setTooltipText(action.tooltipKeyOrText());
        }
        action.createIcon().ifPresent(button::setIcon);
        button.addClickListener(e -> action.onClick().run());

        VaadinActionBinder.bindEnabled(button, action.enabled());
        VaadinActionBinder.bindVisible(button, action.visible());

        return button;
    }

    /**
     * Alias for {@link #from(Action)}.
     */
    public static Button action(Action action) {
        return from(action);
    }

    /**
     * Fluent builder for a Vaadin {@link Button}.
     */
    public static final class Builder {

        private String labelKey;
        private String tooltipKey;
        private Runnable action;
        private EnablementBinding legacyEnablement;
        private ObservableValue<Boolean> enabledState;
        private ObservableValue<Boolean> visibleState;
        private final List<ButtonVariant> themeVariants = new ArrayList<>();
        private TextResolver textResolver = new DefaultTextResolver();
        private int errorNotificationMs = 5_000;

        private Builder() {
        }

        public Builder withTextResolver(TextResolver textResolver) {
            this.textResolver = Objects.requireNonNull(textResolver, "textResolver");
            return this;
        }

        public Builder label(String keyOrText) {
            this.labelKey = keyOrText;
            return this;
        }

        public Builder tooltip(String keyOrText) {
            this.tooltipKey = keyOrText;
            return this;
        }

        /**
         * New Reactive Lite API for button enablement.
         */
        public Builder enabledBy(ObservableValue<Boolean> enabled) {
            this.enabledState = Objects.requireNonNull(enabled, "enabled");
            return this;
        }

        /**
         * New Reactive Lite API for button visibility.
         */
        public Builder visibleBy(ObservableValue<Boolean> visible) {
            this.visibleState = Objects.requireNonNull(visible, "visible");
            return this;
        }

        /**
         * Legacy convenience API for computed enablement.
         *
         * <p>For explicit and predictable behavior, prefer {@link #enabledBy(ObservableValue)}.
         * This method is kept for compatibility.</p>
         */
        public EnablementStep enabledWhen(Supplier<Boolean> condition) {
            if (this.legacyEnablement != null) {
                throw new IllegalStateException("enabledWhen already defined");
            }
            this.legacyEnablement = new EnablementBinding(condition);
            return new EnablementStep(this, legacyEnablement);
        }

        /**
         * Defines the click action.
         */
        public Builder action(Runnable action) {
            if (this.action != null) {
                throw new IllegalStateException("action already defined");
            }
            this.action = action;
            return this;
        }

        public Builder style(ButtonVariant variant) {
            this.themeVariants.add(variant);
            return this;
        }

        public Builder errorNotificationMs(int ms) {
            this.errorNotificationMs = ms;
            return this;
        }

        public Button build() {
            ObservableValue<Boolean> effectiveEnabled = resolveEnabledState();
            var actionBuilder = Actions.create()
                    .label(resolve(defaultIfNull(labelKey, "")))
                    .tooltip(tooltipKey != null ? resolve(tooltipKey) : null)
                    .onClick(action != null ? () -> runSafely(action) : () -> { })
                    .enabledBy(effectiveEnabled);
            if (visibleState != null) {
                actionBuilder.visibleBy(visibleState);
            }
            Action rendered = actionBuilder.build();

            Button button = Buttons.from(rendered);
            themeVariants.forEach(button::addThemeVariants);
            return button;
        }

        private ObservableValue<Boolean> resolveEnabledState() {
            if (enabledState != null && legacyEnablement != null) {
                throw new IllegalStateException("Use either enabledBy(...) or enabledWhen(...), not both");
            }
            if (enabledState != null) {
                return enabledState;
            }
            if (legacyEnablement != null) {
                return legacyEnablement.toState();
            }
            return de.javaholic.toolkit.ui.state.BooleanStates.constant(true);
        }

        private String resolve(String key) {
            String resolved = textResolver.resolve(key).orElse(key);
            return resolved != null ? resolved : key;
        }

        private void runSafely(Runnable action) {
            try {
                action.run();
            } catch (IllegalStateException ex) {
                Notification.show(
                        ex.getMessage(),
                        errorNotificationMs,
                        Notification.Position.BOTTOM_CENTER
                );
            }
        }

        private static String defaultIfNull(String value, String fallback) {
            return value != null ? value : fallback;
        }
    }

    /**
     * Legacy compatibility step for {@link Builder#enabledWhen(Supplier)}.
     */
    public static final class EnablementStep {

        private final Builder builder;
        private final EnablementBinding binding;

        private EnablementStep(Builder builder, EnablementBinding binding) {
            this.builder = builder;
            this.binding = binding;
        }

        public EnablementStep revalidateOn(HasValue<?, ?> field) {
            Objects.requireNonNull(field, "field");
            binding.addRegistrar(r -> field.addValueChangeListener(e -> r.run()));
            return this;
        }

        public EnablementStep revalidateOn(Button button) {
            Objects.requireNonNull(button, "button");
            binding.addRegistrar(r -> button.addClickListener(e -> r.run()));
            return this;
        }

        public EnablementStep revalidateOn(Consumer<Runnable> registrar) {
            Objects.requireNonNull(registrar, "registrar");
            binding.addRegistrar(registrar);
            return this;
        }

        public Builder done() {
            return builder;
        }
    }

    private static final class EnablementBinding {

        private final Supplier<Boolean> condition;
        private final List<Consumer<Runnable>> registrars = new ArrayList<>();

        private EnablementBinding(Supplier<Boolean> condition) {
            this.condition = Objects.requireNonNull(condition, "condition");
        }

        private void addRegistrar(Consumer<Runnable> registrar) {
            registrars.add(Objects.requireNonNull(registrar, "registrar"));
        }

        private ObservableValue<Boolean> toState() {
            Trigger trigger = new Trigger();
            for (Consumer<Runnable> registrar : registrars) {
                registrar.accept(trigger::fire);
            }
            return DerivedState.of(() -> Boolean.TRUE.equals(condition.get()), trigger);
        }
    }
}
