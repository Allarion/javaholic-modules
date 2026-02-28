package de.javaholic.toolkit.iam.persistence.file;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.domain.UserStatus;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileUserFormStoreTest {

    @Test
    public void loadsUsersFromYaml() {
        FileUserFormStore store = new FileUserFormStore("iam/users-test.yaml");
        Optional<User> admin = store.findByIdentifier("admin");

        assertTrue(admin.isPresent());
        assertEquals(UserStatus.ACTIVE, admin.get().getStatus());
        assertEquals("Administrator", admin.get().getDisplayName());
        assertEquals(Set.of("ADMIN"), toRoleNames(admin.get()));

        Role adminRole = admin.get().getRoles().iterator().next();
        assertEquals(Set.of("user.manage", "config.write"), toPermissionCodes(adminRole));
    }

    private Set<String> toRoleNames(User user) {
        return user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet());
    }

    private Set<String> toPermissionCodes(Role role) {
        return role.getPermissions().stream().map(Permission::getCode)
                .collect(java.util.stream.Collectors.toSet());
    }
}
