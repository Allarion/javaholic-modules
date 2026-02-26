package de.javaholic.toolkit.ui.policy;

import de.javaholic.toolkit.iam.core.api.PermissionChecker;
import de.javaholic.toolkit.ui.meta.UiProperty;

import java.util.Objects;

/**
 * Default UI policy implementation.
 *
 * <p>Evaluation order: hidden flag, permission visibility, read-only, required, then enabled.</p>
 */
public final class DefaultUiPolicyEngine implements UiPolicyEngine {

    /**
     * Evaluates permission-only policy for action-level UX enablement.
     */
    public boolean isPermissionAllowed(String permissionKey, UiPolicyContext context) {
        if (permissionKey == null || permissionKey.isBlank()) {
            return true;
        }
        PermissionChecker checker = context != null ? context.getPermissionChecker() : null;
        if (checker == null) {
            return true;
        }
        return checker.hasPermission(permissionKey);
    }

    @Override
    public UiDecision evaluate(UiProperty<?> property, UiPolicyContext context) {
        Objects.requireNonNull(property, "property");

        boolean visible = true;
        if (property.isHidden()) {
            visible = false;
        }

        if (visible && property.permissionKey().isPresent()) {
            PermissionChecker checker = context != null ? context.getPermissionChecker() : null;
            if (checker != null) {
                visible = checker.hasPermission(property.permissionKey().orElseThrow());
            }
        }

        boolean readOnly = property.isReadOnly();
        boolean required = property.isRequired();
        boolean enabled = visible && !readOnly;
        return UiDecision.custom(visible, enabled, readOnly, required);
    }
}
