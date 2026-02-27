package de.javaholic.toolkit.iam.ui;

import com.vaadin.flow.component.notification.Notification;
import de.javaholic.toolkit.iam.core.domain.UserStatus;
import de.javaholic.toolkit.iam.dto.PermissionFormDto;
import de.javaholic.toolkit.iam.dto.RoleFormDto;
import de.javaholic.toolkit.iam.dto.UserFormDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.api.ResourceAction;
import de.javaholic.toolkit.ui.api.UiActionProvider;
import de.javaholic.toolkit.ui.api.UiSurfaceContext;
import de.javaholic.toolkit.ui.resource.action.ResourceActions;
import de.javaholic.toolkit.ui.resource.actionprovider.CrudActionProvider;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class IamUiActions {

    private IamUiActions() {
    }

    public static Class<?> usersCrudProvider() {
        return UsersCrudActionProvider.class;
    }

    public static Class<?> rolesCrudProvider() {
        return CrudActionProvider.class;
    }

    public static Class<?> permissionsCrudProvider() {
        return PermissionsCrudActionProvider.class;
    }

    public static ResourceAction.RowAction<UserFormDto> deactivateUserAction(CrudStore<UserFormDto, UUID> userStore) {
        Objects.requireNonNull(userStore, "userStore");
        return ResourceAction.<UserFormDto>row("Deactivate", user -> {
                    user.setStatus(UserStatus.DISABLED);
                    userStore.save(user);
                })
                .enabledWhen(user -> user.getStatus() == UserStatus.ACTIVE);
    }

    public static ResourceAction.RowAction<UserFormDto> activateUserAction(CrudStore<UserFormDto, UUID> userStore) {
        Objects.requireNonNull(userStore, "userStore");
        return ResourceAction.<UserFormDto>row("Activate", user -> {
                    user.setStatus(UserStatus.ACTIVE);
                    userStore.save(user);
                })
                .enabledWhen(user -> user.getStatus() != UserStatus.ACTIVE);
    }

    public static ResourceAction.RowAction<UserFormDto> assignRolesAction() {
        return ResourceAction.row("Assign Roles...", user ->
                Notification.show("Assign Roles is not implemented yet.", 2500, Notification.Position.MIDDLE)
        );
    }

    public static ResourceAction.RowAction<RoleFormDto> assignPermissionsAction() {
        return ResourceAction.row("Assign Permissions...", role ->
                Notification.show("Assign Permissions is not implemented yet.", 2500, Notification.Position.MIDDLE)
        );
    }

    public static final class UsersCrudActionProvider implements UiActionProvider<UserFormDto> {
        @Override
        public List<ResourceAction<UserFormDto>> actions(UiSurfaceContext<UserFormDto> context) {
            return List.of(
                    ResourceActions.<UserFormDto>toolbar("Create")
                            .onInvoke(context.view()::create)
                            .build(),
                    ResourceActions.<UserFormDto>item("Edit")
                            .onInvoke(context.view()::edit)
                            .build()
            );
        }
    }

    public static final class PermissionsCrudActionProvider implements UiActionProvider<PermissionFormDto> {
        @Override
        public List<ResourceAction<PermissionFormDto>> actions(UiSurfaceContext<PermissionFormDto> context) {
            return List.of(
                    ResourceActions.<PermissionFormDto>toolbar("Create")
                            .onInvoke(context.view()::create)
                            .build(),
                    ResourceActions.<PermissionFormDto>item("Delete")
                            .onInvoke(context.view()::delete)
                            .build()
            );
        }
    }
}
