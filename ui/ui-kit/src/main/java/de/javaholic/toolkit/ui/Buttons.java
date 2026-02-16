package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import de.javaholic.toolkit.i18n.DefaultTextResolver;
import de.javaholic.toolkit.i18n.TextResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Fluent builder for Vaadin {@link Button Buttons}.
 *
 * <p>This utility focuses on explicit behavior:
 * no automatic bindings, no polling, no hidden lifecycle logic.</p>
 *
 * <pre>{@code
 * Button save =
 *     Buttons.create()
 *            .label("save")
 *            .action(this::onSave)
 *            .build();
 * }</pre>
 *
 * <h3>Dynamic enablement</h3>
 *
 * <pre>{@code
 * Button ok =
 *     Buttons.create()
 *            .label("ok")
 *            .enabledWhen(form::isValid)
 *                .revalidateOn(nameField)
 *                .revalidateOn(enabledCheckbox)
 *                .done()
 *            .action(this::onOk)
 *            .build();
 * }</pre>
 *
 * <p>This is optional syntax sugar only. You can always use Vaadin directly.</p>
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

    // =====================================================================
    // == Builder
    // =====================================================================

    /**
     * Fluent builder for a Vaadin {@link Button}.
     *
     * <p>After calling {@link #build()}, the returned button behaves like
     * a regular Vaadin component. The fluent API is detached.</p>
     */
    public static final class Builder {

        private String labelKey;
        private String tooltipKey;
        private Runnable action;
        private EnablementBinding enablement;
        private final List<ButtonVariant> themeVariants = new ArrayList<>();
        // UI boundary: keys are stored in the builder and resolved only when applying to Vaadin.
        private TextResolver textResolver = new DefaultTextResolver();

        /** notification duration for action errors */
        private int errorNotificationMs = 5_000;

        private Builder() {
        }

        public Builder withTextResolver(TextResolver textResolver) {
            this.textResolver = Objects.requireNonNull(textResolver, "textResolver");
            return this;
        }

        /**
         * Sets the button label key.
         */
        public Builder label(String key) {
            this.labelKey = key;
            return this;
        }

        /**
         * Sets the button tooltip key.
         */
        public Builder tooltip(String key) {
            this.tooltipKey = key;
            return this;
        }

        /**
         * Defines a dynamic enablement condition.
         */
        public EnablementStep enabledWhen(Supplier<Boolean> condition) {
            if (this.enablement != null) {
                throw new IllegalStateException("enabledWhen already defined");
            }
            this.enablement = new EnablementBinding(condition);
            return new EnablementStep(this, enablement);
        }

        /**
         * Defines the click action.
         *
         * <p>The action is executed inside a centralized try/catch block
         * handling {@link IllegalStateException} uniformly.</p>
         */
        public Builder action(Runnable action) {
            if (this.action != null) {
                throw new IllegalStateException("action already defined");
            }
            this.action = action;
            return this;
        }

        /**
         * Adds a Vaadin {@link ButtonVariant}.
         */
        public Builder style(ButtonVariant variant) {
            this.themeVariants.add(variant);
            return this;
        }

        /**
         * Indicates how long an error Notification should be displayed.
         */
        public Builder errorNotificationMs(int ms) {
            this.errorNotificationMs = ms;
            return this;
        }

        /**
         * Builds the Vaadin {@link Button}.
         */
        public Button build() {
            Button button = new Button();

            themeVariants.forEach(button::addThemeVariants);

            if (labelKey != null) {
                button.setText(resolve(labelKey));
            }
            if (tooltipKey != null) {
                button.setTooltipText(resolve(tooltipKey));
            }

            if (action != null) {
                button.addClickListener(e -> runSafely(action));
            }

            if (enablement != null) {
                enablement.bindTo(button);
            }

            return button;
        }

        private String resolve(String key) {
            String resolved = textResolver.resolve(key).orElse(key);
                    //?1 .orElseThrow(() -> new IllegalStateException("resolver ought to return 'key' (if not able to resolve) but didn't"));
            return resolved != null ? resolved : key;
        }

        /**
         * Centralized exception handling for button actions.
         */
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
    }

    // =====================================================================
    // == EnablementStep
    // =====================================================================

    public static final class EnablementStep {

        private final Builder builder;
        private final EnablementBinding binding;

        private EnablementStep(Builder builder, EnablementBinding binding) {
            this.builder = builder;
            this.binding = binding;
        }

        /**
         * Revalidates when a Vaadin value component changes.
         */
        public EnablementStep revalidateOn(HasValue<?, ?> field) {
            Objects.requireNonNull(field, "field");
            binding.addRegistrar(r -> field.addValueChangeListener(e -> r.run()));
            return this;
        }

        /**
         * Revalidates when a button is clicked.
         */
        public EnablementStep revalidateOn(Button button) {
            Objects.requireNonNull(button, "button");
            binding.addRegistrar(r -> button.addClickListener(e -> r.run()));
            return this;
        }

        /**
         * Revalidates using a custom registrar (e.g. attach listener, timer, etc.).
         */
        public EnablementStep revalidateOn(Consumer<Runnable> registrar) {
            Objects.requireNonNull(registrar, "registrar");
            binding.addRegistrar(registrar);
            return this;
        }

        /**
         * Returns to the main builder.
         */
        public Builder done() {
            return builder;
        }
    }

    // =====================================================================
    // == EnablementBinding
    // =====================================================================

    private static final class EnablementBinding {

        private final Supplier<Boolean> condition;
        private final List<Consumer<Runnable>> registrars = new ArrayList<>();

        private EnablementBinding(Supplier<Boolean> condition) {
            this.condition = Objects.requireNonNull(condition, "condition");
        }

        private void addRegistrar(Consumer<Runnable> registrar) {
            registrars.add(Objects.requireNonNull(registrar, "registrar"));
        }

        private void bindTo(Button button) {
            Runnable revalidate =
                    () -> button.setEnabled(Boolean.TRUE.equals(condition.get()));

            revalidate.run();

            for (Consumer<Runnable> registrar : registrars) {
                registrar.accept(revalidate);
            }
        }
    }
}
