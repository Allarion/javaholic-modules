package de.javaholic.toolkit.iam.security.spring;

import de.javaholic.toolkit.iam.core.api.CurrentUser;
import de.javaholic.toolkit.iam.core.api.UserPrincipal;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultPermissionCheckerTest {

    @Test
    public void returnsTrueForActiveUserWithPermission() {
        DefaultPermissionChecker checker = new DefaultPermissionChecker(activeUser("config.read"));
        assertTrue(checker.hasPermission("config.read"));
        assertFalse(checker.hasPermission("config.write"));
    }

    @Test
    public void returnsFalseForInactiveUser() {
        DefaultPermissionChecker checker = new DefaultPermissionChecker(inactiveUser("config.read"));
        assertFalse(checker.hasPermission("config.read"));
    }

    private CurrentUser activeUser(String... permissions) {
        return () -> Optional.of(new UserPrincipal(
                UUID.randomUUID(),
                "alice",
                Set.of("ADMIN"),
                Set.of(permissions),
                true
        ));
    }

    private CurrentUser inactiveUser(String... permissions) {
        return () -> Optional.of(new UserPrincipal(
                UUID.randomUUID(),
                "bob",
                Set.of("USER"),
                Set.of(permissions),
                false
        ));
    }
}
