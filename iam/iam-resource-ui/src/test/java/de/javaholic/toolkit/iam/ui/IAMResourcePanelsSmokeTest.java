package de.javaholic.toolkit.iam.ui;

import de.javaholic.toolkit.iam.core.domain.UserStatus;
import de.javaholic.toolkit.iam.dto.PermissionFormDto;
import de.javaholic.toolkit.iam.dto.RoleFormDto;
import de.javaholic.toolkit.iam.dto.UserFormDto;
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
class IAMResourcePanelsSmokeTest {

    @Mock
    private CrudStore<UserFormDto, UUID> userStore;

    @Mock
    private CrudStore<RoleFormDto, UUID> roleDtoStore;

    @Mock
    private CrudStore<PermissionFormDto, UUID> permissionDtoStore;

    @Test
    void usersPanelCreatesAndWiresRoleChoices() {
        RoleFormDto admin = new RoleFormDto("admin", Set.of());
        UserFormDto user = new UserFormDto(UUID.randomUUID(), "anna", UserStatus.ACTIVE, Set.of(admin));
        when(userStore.findAll()).thenReturn(List.of(user));

        var panel = IAMResourcePanels.users(userStore);

        assertThat(panel).isNotNull();
        verify(userStore, atLeastOnce()).findAll();
    }

    @Test
    void rolesPanelCreatesAndWiresPermissionChoices() {
        PermissionFormDto permission = new PermissionFormDto("user.read");
        when(roleDtoStore.findAll()).thenReturn(List.of(new RoleFormDto("reader", Set.of(permission))));

        var panel = IAMResourcePanels.roles(roleDtoStore, permissionDtoStore);

        assertThat(panel).isNotNull();
        verify(roleDtoStore, atLeastOnce()).findAll();
        verify(permissionDtoStore, never()).findAll();
    }

    @Test
    void permissionsPanelCreatesAndLoadsItems() {
        PermissionFormDto permission = new PermissionFormDto("config.read");
        when(permissionDtoStore.findAll()).thenReturn(List.of(permission));
        var panel = IAMResourcePanels.permissions(permissionDtoStore);
        assertThat(panel).isNotNull();
        verify(permissionDtoStore, atLeastOnce()).findAll();
    }
}


