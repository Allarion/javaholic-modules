package de.javaholic.toolkit.iam.security.spring;

import de.javaholic.toolkit.iam.core.api.PermissionChecker;
import de.javaholic.toolkit.iam.core.api.RequiresPermission;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;

import java.util.Objects;

@Aspect
public final class AspectRequiresPermission {

    private final PermissionChecker permissionChecker;

    public AspectRequiresPermission(PermissionChecker permissionChecker) {
        this.permissionChecker = Objects.requireNonNull(permissionChecker, "permissionChecker");
    }

    @Before("@annotation(requiresPermission)")
    public void requireMethodPermission(RequiresPermission requiresPermission) {
        ensurePermission(requiresPermission.value());
    }

    @Before("@within(requiresPermission)")
    public void requireTypePermission(RequiresPermission requiresPermission) {
        ensurePermission(requiresPermission.value());
    }

    private void ensurePermission(String permission) {
        if (!permissionChecker.hasPermission(permission)) {
            throw new AccessDeniedException("Missing permission: " + permission);
        }
    }
}
