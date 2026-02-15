package de.javaholic.toolkit.iam.ui;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.ui.adapter.PermissionCrudStoreAdapter;
import de.javaholic.toolkit.iam.ui.adapter.RoleCrudStoreAdapter;
import de.javaholic.toolkit.iam.ui.adapter.UserCrudStoreAdapter;
import de.javaholic.toolkit.ui.Inputs;
import de.javaholic.toolkit.ui.crud.CrudPanel;
import de.javaholic.toolkit.ui.crud.CrudPanels;
import de.javaholic.toolkit.ui.form.Forms;

import java.util.Objects;

public final class IAMCrudPanels {

    private IAMCrudPanels() {
    }

    public static CrudPanel<User> users(UserCrudStoreAdapter userStore, RoleCrudStoreAdapter roleStore) {
        return users(userStore, roleStore, Labels.defaults());
    }

    public static CrudPanel<User> users(UserCrudStoreAdapter userStore, RoleCrudStoreAdapter roleStore, Labels labels) {
        Objects.requireNonNull(userStore, "userStore");
        Objects.requireNonNull(roleStore, "roleStore");
        Labels effective = Labels.defaults().merge(labels);

        MultiSelectComboBox<Role> roleField = Inputs.multiselect(Role.class).build();
        roleField.setItems(roleStore.findAll());

        return CrudPanels.of(User.class)
                .withStore(userStore)
                .withFormBuilderFactory(() ->
                        Forms.of(User.class)
                                .field("username", field -> field.label(effective.userUsername()))
                                .field("status", field -> field.label(effective.userStatus()))
                                .field("roles", field -> {
                                    field.component(roleField);
                                    field.label(effective.userRoles());
                                }))
                .build();
    }

    public static CrudPanel<Role> roles(RoleCrudStoreAdapter roleStore, PermissionCrudStoreAdapter permissionStore) {
        return roles(roleStore, permissionStore, Labels.defaults());
    }

    public static CrudPanel<Role> roles(RoleCrudStoreAdapter roleStore, PermissionCrudStoreAdapter permissionStore, Labels labels) {
        Labels effective = Labels.defaults().merge(labels);

        MultiSelectComboBox<Permission> permissionField = Inputs.multiselect(Permission.class).build();
        permissionField.setItems(permissionStore.findAll());
        return CrudPanels.of(Role.class)
                .withStore(roleStore)
                .withFormBuilderFactory(() -> Forms.of(Role.class)
                        .field("name", field -> field.label(effective.roleName()))
                        .field("permissions", field -> {
                            field.component(permissionField);
                            field.label(effective.rolePermissions());
                        }))
                .build();
    }

    public static CrudPanel<Permission> permissions(PermissionCrudStoreAdapter permissionStore) {
        return permissions(permissionStore, Labels.defaults());
    }

    public static CrudPanel<Permission> permissions(PermissionCrudStoreAdapter permissionStore, Labels labels) {
        Objects.requireNonNull(permissionStore, "permissionStore");
        Labels effective = Labels.defaults().merge(labels);

        return CrudPanels.of(Permission.class)
                .withStore(permissionStore)
                .withFormBuilderFactory(() -> Forms.of(Permission.class)
                        .field("code", field -> field.label(effective.permissionName())))
                .build();
    }
    
    public record Labels(String userUsername, String userStatus, String userRoles, String roleName,
                         String rolePermissions, String permissionName) {

        // TODO: revisit i18n keys!
        public static Labels defaults() {
            return new Labels("user.username", "user.status", "user.roles", "role.name", "role.permissions", "permission.name");
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
