package de.javaholic.toolkit.iam.ui;

import de.javaholic.toolkit.iam.ui.dto.PermissionDto;
import de.javaholic.toolkit.iam.ui.dto.RoleDto;
import de.javaholic.toolkit.iam.ui.dto.UserDto;
import de.javaholic.toolkit.iam.ui.adapter.RoleCrudStoreAdapter;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.crud.CrudPanel;
import de.javaholic.toolkit.ui.crud.CrudPanels;

import java.util.Objects;
import java.util.UUID;

public final class IAMCrudPanels {

    private IAMCrudPanels() {
    }

    public static CrudPanel<UserDto> users(CrudStore<UserDto, UUID> userStore, RoleCrudStoreAdapter roleStore) {
        return users(userStore, roleStore, Labels.defaults());
    }

    public static CrudPanel<UserDto> users(CrudStore<UserDto, UUID> userStore, RoleCrudStoreAdapter roleStore, Labels labels) {
        Objects.requireNonNull(userStore, "userStore");
        Objects.requireNonNull(roleStore, "roleStore");
        Labels effective = Labels.defaults().merge(labels);
        return CrudPanels.auto(UserDto.class)
                .withStore(userStore)
                .override("username", cfg -> cfg.label(effective.userUsername()))
                .override("status", cfg -> cfg.label(effective.userStatus()))
                .override("roles", cfg -> cfg.label(effective.userRoles()))
                .build();
    }

    public static CrudPanel<RoleDto> roles(CrudStore<RoleDto, UUID> roleStore, CrudStore<PermissionDto, UUID> permissionStore) {
        return roles(roleStore, permissionStore, Labels.defaults());
    }

    public static CrudPanel<RoleDto> roles(CrudStore<RoleDto, UUID> roleStore, CrudStore<PermissionDto, UUID> permissionStore, Labels labels) {
        Objects.requireNonNull(roleStore, "roleStore");
        Objects.requireNonNull(permissionStore, "permissionStore");
        Labels effective = Labels.defaults().merge(labels);
        return CrudPanels.auto(RoleDto.class)
                .withStore(roleStore)
                .override("name", cfg -> cfg.label(effective.roleName()))
                .override("permissions", cfg -> cfg.label(effective.rolePermissions()))
                .build();
    }

    public static CrudPanel<PermissionDto> permissions(CrudStore<PermissionDto, UUID> permissionStore) {
        return permissions(permissionStore, Labels.defaults());
    }

    public static CrudPanel<PermissionDto> permissions(CrudStore<PermissionDto, UUID> permissionStore, Labels labels) {
        Objects.requireNonNull(permissionStore, "permissionStore");
        Labels effective = Labels.defaults().merge(labels);
        return CrudPanels.auto(PermissionDto.class)
                .withStore(permissionStore)
                .override("code", cfg -> cfg.label(effective.permissionName()))
                .build();
    }
    
    public record Labels(String userUsername, String userStatus, String userRoles, String roleName,
                         String rolePermissions, String permissionName) {

        // TODO: revisit i18n keys!
        public static Labels defaults() {
            return new Labels(
                "form.user.email.label",
                "form.user.active.label",
                "form.user.roles.label",
                "form.role.name.label",
                "form.role.permissions.label",
                "form.permission.code.label"
            );
        }

        public Labels merge(Labels override) {
            if (override == null) {
                return this;
            }
            return new Labels(valueOrDefault(override.userUsername, userUsername), valueOrDefault(override.userStatus, userStatus), valueOrDefault(override.userRoles, userRoles), valueOrDefault(override.roleName, roleName), valueOrDefault(override.rolePermissions, rolePermissions), valueOrDefault(override.permissionName, permissionName));
        }

        private static String valueOrDefault(String value, String fallback) {
            return value == null || value.isBlank() ? fallback : value;
        }
    }
}
