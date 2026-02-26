package de.javaholic.toolkit.iam.ui;

import com.vaadin.flow.component.notification.Notification;
import de.javaholic.toolkit.iam.core.domain.UserStatus;
import de.javaholic.toolkit.iam.dto.RoleFormDto;
import de.javaholic.toolkit.iam.dto.UserFormDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.crud.action.CrudAction;
import de.javaholic.toolkit.ui.crud.action.CrudPreset;

import java.util.Objects;
import java.util.UUID;

/**
 * IAM-specific CRUD defaults and action definitions.
 *
 * <p>Presets configure default create/edit/delete actions only. Callers can still add
 * additional custom actions through the CRUD builder API.</p>
 */
public final class IamUiPresets {

    private static final CrudPreset USERS = new FixedPreset(true, true, false);
    private static final CrudPreset ROLES = new FixedPreset(true, true, true);
    private static final CrudPreset PERMISSIONS = new FixedPreset(false, false, false);

    private IamUiPresets() {
    }

    public static <T> CrudPreset users() {
        return USERS;
    }

    public static <T> CrudPreset roles() {
        return ROLES;
    }

    public static <T> CrudPreset permissions() {
        return PERMISSIONS;
    }

    public static CrudAction.RowAction<UserFormDto> deactivateUserAction(CrudStore<UserFormDto, UUID> userStore) {
        Objects.requireNonNull(userStore, "userStore");
        return CrudAction.<UserFormDto>row("Deactivate", user -> {
                    user.setStatus(UserStatus.DISABLED);
                    userStore.save(user);
                })
                .enabledWhen(user -> user.getStatus() == UserStatus.ACTIVE);
    }

    public static CrudAction.RowAction<UserFormDto> activateUserAction(CrudStore<UserFormDto, UUID> userStore) {
        Objects.requireNonNull(userStore, "userStore");
        return CrudAction.<UserFormDto>row("Activate", user -> {
                    user.setStatus(UserStatus.ACTIVE);
                    userStore.save(user);
                })
                .enabledWhen(user -> user.getStatus() != UserStatus.ACTIVE);
    }

    public static CrudAction.RowAction<UserFormDto> assignRolesAction() {
        return CrudAction.row("Assign Roles...", user ->
                Notification.show("Assign Roles is not implemented yet.", 2500, Notification.Position.MIDDLE)
        );
    }

    public static CrudAction.RowAction<RoleFormDto> assignPermissionsAction() {
        return CrudAction.row("Assign Permissions...", role ->
                Notification.show("Assign Permissions is not implemented yet.", 2500, Notification.Position.MIDDLE)
        );
    }

    private record FixedPreset(boolean enableCreate, boolean enableEdit, boolean enableDelete) implements CrudPreset {
    }
}
