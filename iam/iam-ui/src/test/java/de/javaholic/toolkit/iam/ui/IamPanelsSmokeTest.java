package de.javaholic.toolkit.iam.ui;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.domain.UserStatus;
import de.javaholic.toolkit.iam.ui.adapter.PermissionCrudStoreAdapter;
import de.javaholic.toolkit.iam.ui.adapter.RoleCrudStoreAdapter;
import de.javaholic.toolkit.iam.ui.adapter.UserCrudStoreAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamPanelsSmokeTest {

    @Mock
    private UserCrudStoreAdapter userStore;

    @Mock
    private RoleCrudStoreAdapter roleStore;

    @Mock
    private PermissionCrudStoreAdapter permissionStore;

    @Test
    void usersPanelCreatesAndWiresRoleChoices() {
        Role admin = new Role("admin", Set.of());
        User user = new User(UUID.randomUUID(), "anna", UserStatus.ACTIVE, Set.of(admin));
        when(userStore.findAll()).thenReturn(List.of(user));
        when(roleStore.findAll()).thenReturn(List.of(admin));

        var panel = IamPanels.users(userStore, roleStore);

        assertThat(panel).isNotNull();
        verify(userStore, atLeastOnce()).findAll();
        verify(roleStore).findAll();
    }

    @Test
    void rolesPanelCreatesAndWiresPermissionChoices() {
        Permission permission = new Permission("user.read");
        when(roleStore.findAll()).thenReturn(List.of(new Role("reader", Set.of(permission))));
        when(permissionStore.findAll()).thenReturn(List.of(permission));

        var panel = IamPanels.roles(roleStore,permissionStore);

        assertThat(panel).isNotNull();
        verify(roleStore, atLeastOnce()).findAll();
        verify(permissionStore).findAll();
    }

    @Test
    void permissionsPanelCreatesAndLoadsItems() {
        Permission permission = new Permission("config.read");
        when(permissionStore.findAll()).thenReturn(List.of(permission));
        var panel = IamPanels.permissions(permissionStore);
        assertThat(panel).isNotNull();
        verify(permissionStore, atLeastOnce()).findAll();
    }
}
