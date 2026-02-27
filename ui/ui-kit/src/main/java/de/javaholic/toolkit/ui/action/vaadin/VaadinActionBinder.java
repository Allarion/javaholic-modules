package de.javaholic.toolkit.ui.action.vaadin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.UI;
import de.javaholic.toolkit.iam.core.api.PermissionChecker;
import de.javaholic.toolkit.ui.action.Actions;
import de.javaholic.toolkit.ui.policy.DefaultUiPolicyEngine;
import de.javaholic.toolkit.ui.policy.UiPolicyContext;
import de.javaholic.toolkit.ui.state.DerivedState;
import de.javaholic.toolkit.ui.state.ObservableValue;
import de.javaholic.toolkit.ui.state.Subscription;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Binds reactive action state to Vaadin component lifecycle.
 *
 * <p>Subscriptions are disposed on component detach to avoid leaks.</p>
 *
 * <p>Threading rule: no background worker support is provided. Updates are
 * applied in current UI context; if needed, {@link UI#access(Runnable)} is used
 * as a safe fallback.</p>
 */
public final class VaadinActionBinder {

    private static final DefaultUiPolicyEngine POLICY_ENGINE = new DefaultUiPolicyEngine();

    private VaadinActionBinder() {
    }

    // TODO: more comments & examples. also check side effects if bindEndabled

    /**
     * Binds enabled state when component supports {@link HasEnabled}.
     */
    public static void bindEnabled(Component component, ObservableValue<Boolean> enabled) {
        Objects.requireNonNull(component, "component");
        Objects.requireNonNull(enabled, "enabled");
        if (component instanceof HasEnabled hasEnabled) {
            bind(component, enabled, v -> hasEnabled.setEnabled(Boolean.TRUE.equals(v)));
        }
    }

    /**
     * Binds enabled state with permission-aware precondition.
     *
     * <p>Order is explicit: permission check first, then existing action enabled state.</p>
     */
    public static void bindEnabled(Component component, Actions.Action action, PermissionChecker permissionChecker) {
        Objects.requireNonNull(component, "component");
        Objects.requireNonNull(action, "action");
        boolean permissionAllowed = action.permissionKey()
                .map(key -> POLICY_ENGINE.isPermissionAllowed(key, new UiPolicyContext(permissionChecker, null)))
                .orElse(true);
        ObservableValue<Boolean> effectiveEnabled = DerivedState.of(
                () -> permissionAllowed && Boolean.TRUE.equals(action.enabled().get()),
                action.enabled()
        );
        bindEnabled(component, effectiveEnabled);
    }

    /**
     * Binds component visibility.
     */
    public static void bindVisible(Component component, ObservableValue<Boolean> visible) {
        Objects.requireNonNull(component, "component");
        Objects.requireNonNull(visible, "visible");
        bind(component, visible, v -> component.setVisible(Boolean.TRUE.equals(v)));
    }

    private static <T> void bind(Component component, ObservableValue<T> state, Consumer<T> apply) {
        runInUi(component, () -> apply.accept(state.get()));
        Subscription subscription = state.subscribe(value -> runInUi(component, () -> apply.accept(value)));
        component.addDetachListener(event -> subscription.unsubscribe());
    }

    private static void runInUi(Component component, Runnable task) {
        UI ui = component.getUI().orElse(UI.getCurrent());
        if (ui == null) {
            task.run();
            return;
        }
        if (UI.getCurrent() == ui) {
            task.run();
            return;
        }
        ui.access(() -> task.run());
    }
}
