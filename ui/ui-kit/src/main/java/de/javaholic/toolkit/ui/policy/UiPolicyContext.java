package de.javaholic.toolkit.ui.policy;

import de.javaholic.toolkit.iam.core.api.PermissionChecker;

/**
 * Runtime context for UI policy evaluation.
 */
public final class UiPolicyContext {

    private final PermissionChecker permissionChecker;
    private final Object bean;

    public UiPolicyContext(PermissionChecker permissionChecker, Object bean) {
        this.permissionChecker = permissionChecker;
        this.bean = bean;
    }

    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    public Object getBean() {
        return bean;
    }
}
