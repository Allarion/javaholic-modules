package de.javaholic.toolkit.iam.ui;

import com.vaadin.flow.component.notification.Notification;
import de.javaholic.toolkit.iam.core.domain.UserStatus;
import de.javaholic.toolkit.iam.dto.RoleFormDto;
import de.javaholic.toolkit.iam.dto.UserFormDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.resource.action.ResourceAction;
import de.javaholic.toolkit.ui.resource.action.ResourcePreset;

import java.util.Objects;
import java.util.UUID;

/**
 * IAM-specific CRUD defaults and action definitions.
 *
 * <p>Presets configure default create/edit/delete actions only. Callers can still add
 * additional custom actions through the CRUD builder API.</p>
 */
public final class IamUiPresets {

    private static final ResourcePreset USERS = new FixedPreset(true, true, false);
    private static final ResourcePreset ROLES = new FixedPreset(true, true, true);
    private static final ResourcePreset PERMISSIONS = new FixedPreset(true, false, true);

    private IamUiPresets() {
    }

    public static <T> ResourcePreset users() {
        return USERS;
    }

    public static <T> ResourcePreset roles() {
        return ROLES;
    }

    public static <T> ResourcePreset permissions() {
        return PERMISSIONS;
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

    private record FixedPreset(boolean enableCreate, boolean enableEdit, boolean enableDelete) implements ResourcePreset {
    }
}

