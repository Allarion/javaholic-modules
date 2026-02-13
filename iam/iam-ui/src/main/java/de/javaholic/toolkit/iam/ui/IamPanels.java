package de.javaholic.toolkit.iam.ui;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import de.javaholic.toolkit.i18n.Texts;
import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.iam.ui.adapter.PermissionCrudStoreAdapter;
import de.javaholic.toolkit.iam.ui.adapter.RoleCrudStoreAdapter;
import de.javaholic.toolkit.iam.ui.adapter.UserCrudStoreAdapter;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.Inputs;
import de.javaholic.toolkit.ui.crud.CrudPanel;
import de.javaholic.toolkit.ui.form.Forms;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class IamPanels {

    private final CrudStore<User, UUID> userStore;
    private final CrudStore<Role, UUID> roleStore;
    private final CrudStore<Permission, UUID> permissionStore;

    // TODO: Naming sucks
    public IamPanels(CrudStore<User, UUID> userStore, CrudStore<Role, UUID> roleStore, CrudStore<Permission, UUID> permissionStore) {
        this.userStore = userStore;
        this.roleStore = roleStore;
        this.permissionStore = permissionStore;
    }

    public IamPanels(UserStore userStore,
                     RoleStore roleStore,
                     PermissionStore permissionStore) {

        this.userStore = new UserCrudStoreAdapter(userStore);
        this.roleStore = new RoleCrudStoreAdapter(roleStore);
        this.permissionStore = new PermissionCrudStoreAdapter(permissionStore);
    }

    public CrudPanel<User> users() {
        return users(Labels.defaults());
    }

    public CrudPanel<User> users(Labels labels) {
        Objects.requireNonNull(userStore, "userStore");
        Objects.requireNonNull(roleStore, "roleStore");
        Labels effective = Labels.defaults().merge(labels);

        MultiSelectComboBox<Role> roleField = Inputs.multiselect(Role.class).build();
        roleField.setItems(roleStore.findAll());

        CrudPanel<User> panel = CrudPanel.of(User.class, userStore);
        panel.withFormBuilderFactory(() -> Forms.of(User.class).field("username", field -> field.label(Texts.label(effective.userUsername()))).field("status", field -> field.label(Texts.label(effective.userStatus()))).field("roles", field -> {
            field.component(roleField);
            field.label(Texts.label(effective.userRoles()));
        }));
        return panel;
    }

    public CrudPanel<Role> roles() {
        return roles(Labels.defaults());
    }

    public CrudPanel<Role> roles(Labels labels) {
        Labels effective = Labels.defaults().merge(labels);

        MultiSelectComboBox<Permission> permissionField = Inputs.multiselect(Permission.class).build();
        permissionField.setItems(permissionStore.findAll());
        CrudPanel<Role> panel = CrudPanel.of(Role.class, roleStore);
        panel.withFormBuilderFactory(() -> Forms.of(Role.class).field("name", field -> field.label(Texts.label(effective.roleName()))).field("permissions", field -> {
            field.component(permissionField);
            field.label(Texts.label(effective.rolePermissions()));
        }));
        return panel;
    }

    public CrudPanel<Permission> permissions() {
        return permissions(Labels.defaults());
    }

    public CrudPanel<Permission> permissions(Labels labels) {
        Objects.requireNonNull(permissionStore, "permissionStore");
        Labels effective = Labels.defaults().merge(labels);

        CrudPanel<Permission> panel = CrudPanel.of(Permission.class, permissionStore);
        panel.withFormBuilderFactory(() -> Forms.of(Permission.class).field("code", field -> field.label(Texts.label(effective.permissionName()))));
        return panel;
    }

    // TODO: revisit this vs Texts...
    public record Labels(String userUsername, String userStatus, String userRoles, String roleName,
                         String rolePermissions, String permissionName) {

        // TODO: revisit!
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
