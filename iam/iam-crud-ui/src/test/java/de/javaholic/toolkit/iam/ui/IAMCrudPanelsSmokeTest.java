package de.javaholic.toolkit.iam.ui;

import de.javaholic.toolkit.iam.core.domain.UserStatus;
import de.javaholic.toolkit.iam.dto.PermissionDto;
import de.javaholic.toolkit.iam.dto.RoleDto;
import de.javaholic.toolkit.iam.dto.UserDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
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
class IAMCrudPanelsSmokeTest {

    @Mock
    private CrudStore<UserDto, UUID> userStore;

    @Mock
    private CrudStore<RoleDto, UUID> roleDtoStore;

    @Mock
    private CrudStore<PermissionDto, UUID> permissionDtoStore;

    @Test
    void usersPanelCreatesAndWiresRoleChoices() {
        RoleDto admin = new RoleDto("admin", Set.of());
        UserDto user = new UserDto(UUID.randomUUID(), "anna", UserStatus.ACTIVE, Set.of(admin));
        when(userStore.findAll()).thenReturn(List.of(user));

        var panel = IAMCrudPanels.users(userStore);

        assertThat(panel).isNotNull();
        verify(userStore, atLeastOnce()).findAll();
    }

    @Test
    void rolesPanelCreatesAndWiresPermissionChoices() {
        PermissionDto permission = new PermissionDto("user.read");
        when(roleDtoStore.findAll()).thenReturn(List.of(new RoleDto("reader", Set.of(permission))));

        var panel = IAMCrudPanels.roles(roleDtoStore, permissionDtoStore);

        assertThat(panel).isNotNull();
        verify(roleDtoStore, atLeastOnce()).findAll();
        verify(permissionDtoStore, never()).findAll();
    }

    @Test
    void permissionsPanelCreatesAndLoadsItems() {
        PermissionDto permission = new PermissionDto("config.read");
        when(permissionDtoStore.findAll()).thenReturn(List.of(permission));
        var panel = IAMCrudPanels.permissions(permissionDtoStore);
        assertThat(panel).isNotNull();
        verify(permissionDtoStore, atLeastOnce()).findAll();
    }
}

