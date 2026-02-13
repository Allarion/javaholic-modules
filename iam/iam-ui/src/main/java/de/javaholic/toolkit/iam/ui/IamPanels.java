package de.javaholic.toolkit.iam.ui;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import de.javaholic.toolkit.i18n.Texts;
import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.Inputs;
import de.javaholic.toolkit.ui.crud.CrudPanel;
import de.javaholic.toolkit.ui.form.Forms;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class IamPanels {

    private IamPanels() {
    }

    public static CrudPanel<User> users(UserStore userStore, RoleStore roleStore) {
        return users(userStore, roleStore, Labels.defaults());
    }

    public static CrudPanel<Role> roles(RoleStore roleStore, PermissionStore permissionStore) {
        return roles(roleStore, permissionStore, Labels.defaults());
    }

    public static CrudPanel<Permission> permissions(PermissionStore permissionStore) {
        return permissions(permissionStore, Labels.defaults());
    }

    public static CrudPanel<User> users(UserStore userStore, RoleStore roleStore, Labels labels) {
        Objects.requireNonNull(userStore, "userStore");
        Objects.requireNonNull(roleStore, "roleStore");
        Labels effective = Labels.defaults().merge(labels);

        MultiSelectComboBox<Role> roleField = Inputs.multiselect(Role.class)
                .items(roleStore.findAll())
                .build();

        CrudPanel<User> panel = CrudPanel.of(User.class, asCrudUserStore(userStore));
        panel.withFormBuilderFactory(() -> Forms.of(User.class)
                .field("username", field -> field.label(Texts.label(effective.userUsername())))
                .field("status", field -> field.label(Texts.label(effective.userStatus())))
                .field("roles", field -> {
                    field.component(roleField);
                    field.label(Texts.label(effective.userRoles()));
                }));
        return panel;
    }

    public static CrudPanel<Role> roles(RoleStore roleStore, PermissionStore permissionStore, Labels labels) {
        Objects.requireNonNull(roleStore, "roleStore");
        Objects.requireNonNull(permissionStore, "permissionStore");
        Labels effective = Labels.defaults().merge(labels);

        MultiSelectComboBox<Permission> permissionField = Inputs.multiselect(Permission.class)
                .items(permissionStore.findAll())
                .build();

        CrudPanel<Role> panel = CrudPanel.of(Role.class, asCrudRoleStore(roleStore));
        panel.withFormBuilderFactory(() -> Forms.of(Role.class)
                .field("name", field -> field.label(Texts.label(effective.roleName())))
                .field("permissions", field -> {
                    field.component(permissionField);
                    field.label(Texts.label(effective.rolePermissions()));
                }));
        return panel;
    }

    public static CrudPanel<Permission> permissions(PermissionStore permissionStore, Labels labels) {
        Objects.requireNonNull(permissionStore, "permissionStore");
        Labels effective = Labels.defaults().merge(labels);

        CrudPanel<Permission> panel = CrudPanel.of(Permission.class, asCrudPermissionStore(permissionStore));
        panel.withFormBuilderFactory(() -> Forms.of(Permission.class)
                .field("code", field -> field.label(Texts.label(effective.permissionName()))));
        return panel;
    }

    @SuppressWarnings("unchecked")
    private static CrudStore<User, UUID> asCrudUserStore(UserStore store) {
        if (store instanceof CrudStore<?, ?> crudStore) {
            return (CrudStore<User, UUID>) crudStore;
        }
        return new ReadOnlyUserCrudStore(store);
    }

    @SuppressWarnings("unchecked")
    private static CrudStore<Role, String> asCrudRoleStore(RoleStore store) {
        if (store instanceof CrudStore<?, ?> crudStore) {
            return (CrudStore<Role, String>) crudStore;
        }
        return new ReadOnlyRoleCrudStore(store);
    }

    @SuppressWarnings("unchecked")
    private static CrudStore<Permission, String> asCrudPermissionStore(PermissionStore store) {
        if (store instanceof CrudStore<?, ?> crudStore) {
            return (CrudStore<Permission, String>) crudStore;
        }
        return new ReadOnlyPermissionCrudStore(store);
    }

    public record Labels(
            String userUsername,
            String userStatus,
            String userRoles,
            String roleName,
            String rolePermissions,
            String permissionName
    ) {

        // TODO: revisit!
        public static Labels defaults() {
            return new Labels(
                    "user.username",
                    "user.status",
                    "user.roles",
                    "role.name",
                    "role.permissions",
                    "permission.name"
            );
        }

        public Labels merge(Labels override) {
            if (override == null) {
                return this;
            }
            return new Labels(
                    valueOrDefault(override.userUsername, userUsername),
                    valueOrDefault(override.userStatus, userStatus),
                    valueOrDefault(override.userRoles, userRoles),
                    valueOrDefault(override.roleName, roleName),
                    valueOrDefault(override.rolePermissions, rolePermissions),
                    valueOrDefault(override.permissionName, permissionName)
            );
        }

        private static String valueOrDefault(String value, String fallback) {
            return value == null || value.isBlank() ? fallback : value;
        }
    }

    private static final class ReadOnlyUserCrudStore implements CrudStore<User, UUID> {
        private final UserStore delegate;

        private ReadOnlyUserCrudStore(UserStore delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<User> findAll() {
            return delegate.findAll();
        }

        @Override
        public Optional<User> findById(UUID id) {
            return delegate.findAll()
                    .stream()
                    .filter(user -> user.getId().equals(id))
                    .findFirst();
        }

        @Override
        public User save(User entity) {
            throw unsupported();
        }

        @Override
        public void delete(User entity) {
            throw unsupported();
        }
    }

    private static final class ReadOnlyRoleCrudStore implements CrudStore<Role, String> {
        private final RoleStore delegate;

        private ReadOnlyRoleCrudStore(RoleStore delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<Role> findAll() {
            return delegate.findAll();
        }

        @Override
        public Optional<Role> findById(String id) {
            return delegate.findByName(id);
        }

        @Override
        public Role save(Role entity) {
            throw unsupported();
        }

        @Override
        public void delete(Role entity) {
            throw unsupported();
        }
    }

    private static final class ReadOnlyPermissionCrudStore implements CrudStore<Permission, String> {
        private final PermissionStore delegate;

        private ReadOnlyPermissionCrudStore(PermissionStore delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<Permission> findAll() {
            return delegate.findAll();
        }

        @Override
        public Optional<Permission> findById(String id) {
            return delegate.findByCode(id);
        }

        @Override
        public Permission save(Permission entity) {
            throw unsupported();
        }

        @Override
        public void delete(Permission entity) {
            throw unsupported();
        }
    }

    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException(
                "Provided store does not implement CrudStore. " +
                        "Pass a store that implements both IAM store SPI and CrudStore to enable write operations."
        );
    }
}
