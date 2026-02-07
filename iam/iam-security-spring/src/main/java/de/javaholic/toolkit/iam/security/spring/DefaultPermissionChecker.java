package de.javaholic.toolkit.iam.security.spring;

import de.javaholic.toolkit.iam.core.api.CurrentUser;
import de.javaholic.toolkit.iam.core.api.PermissionChecker;
import de.javaholic.toolkit.iam.core.api.UserPrincipal;

import java.util.Objects;
import java.util.Optional;

public final class DefaultPermissionChecker implements PermissionChecker {

    private final CurrentUser currentUser;

    public DefaultPermissionChecker(CurrentUser currentUser) {
        this.currentUser = Objects.requireNonNull(currentUser, "currentUser");
    }

    @Override
    public boolean hasPermission(String permission) {
        if (permission == null || permission.isBlank()) {
            return false;
        }
        Optional<UserPrincipal> principal = currentUser.get();
        return principal.filter(UserPrincipal::isActive)
                .map(UserPrincipal::getPermissions)
                .map(perms -> perms.contains(permission))
                .orElse(false);
    }
}
