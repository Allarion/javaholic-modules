package de.javaholic.toolkit.iam.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.iam.dto.PermissionFormDto;
import de.javaholic.toolkit.iam.dto.RoleFormDto;
import de.javaholic.toolkit.iam.dto.UserFormDto;
import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.resource.GridFormsResourceView;
import de.javaholic.toolkit.ui.resource.ResourcePanels;

import java.util.Objects;
import java.util.UUID;

public final class IAMResourcePanels {

    private IAMResourcePanels() {
    }

    public static Component createView(
            CrudStore<UserFormDto, UUID> userStore,
            CrudStore<RoleFormDto, UUID> roleStore,
            CrudStore<PermissionFormDto, UUID> permissionStore,
            TextResolver textResolver
    ) {
        return createView(userStore, roleStore, permissionStore,textResolver, Labels.defaults());
    }

    public static Component createView(
            CrudStore<UserFormDto, UUID> userStore,
            CrudStore<RoleFormDto, UUID> roleStore,
            CrudStore<PermissionFormDto, UUID> permissionStore,
            TextResolver textResolver,
            Labels labels
    ) {
        Objects.requireNonNull(userStore, "userStore");
        Objects.requireNonNull(roleStore, "roleStore");
        Objects.requireNonNull(permissionStore, "permissionStore");

        Objects.requireNonNull(textResolver, "permissionStore");
        // TODO: actually USE textResolver (and i18n...)

        GridFormsResourceView<UserFormDto> usersPanel = users(userStore, labels);
        GridFormsResourceView<RoleFormDto> rolesPanel = roles(roleStore, permissionStore, labels);
        GridFormsResourceView<PermissionFormDto> permissionsPanel = permissions(permissionStore, labels);

        Tabs tabs = new Tabs(
                new Tab("Users"),
                new Tab("Roles"),
                new Tab("Permissions")
        );

        Div content = new Div();
        content.setSizeFull();

        VerticalLayout layout = new VerticalLayout(tabs, content);
        layout.setSizeFull();
        layout.expand(content);

        Runnable showSelected = () -> {
            content.removeAll();
            switch (tabs.getSelectedIndex()) {
                case 0 -> content.add(usersPanel);
                case 1 -> content.add(rolesPanel);
                case 2 -> content.add(permissionsPanel);
                default -> content.add(usersPanel);
            }
        };

        tabs.addSelectedChangeListener(event -> showSelected.run());
        tabs.setSelectedIndex(0);
        showSelected.run();
        return layout;
    }

    public static GridFormsResourceView<UserFormDto> users(CrudStore<UserFormDto, UUID> userStore) {
        return users(userStore, Labels.defaults());
    }

    public static GridFormsResourceView<UserFormDto> users(CrudStore<UserFormDto, UUID> userStore, Labels labels) {
        Objects.requireNonNull(userStore, "userStore");
        Labels effective = Labels.defaults().merge(labels);
        return ResourcePanels.auto(UserFormDto.class)
                .withStore(userStore)
                .actionProvider(IamUiActions.usersCrudProvider())
                .override("username", cfg -> cfg.label(effective.userUsername()))
                .override("status", cfg -> cfg.label(effective.userStatus()))
                .override("roles", cfg -> cfg.label(effective.userRoles()))
                .rowAction(IamUiActions.deactivateUserAction(userStore))
                .rowAction(IamUiActions.activateUserAction(userStore))
                .rowAction(IamUiActions.assignRolesAction())
                .build();
    }

    public static GridFormsResourceView<RoleFormDto> roles(CrudStore<RoleFormDto, UUID> roleStore, CrudStore<PermissionFormDto, UUID> permissionStore) {
        return roles(roleStore, permissionStore, Labels.defaults());
    }

    public static GridFormsResourceView<RoleFormDto> roles(CrudStore<RoleFormDto, UUID> roleStore, CrudStore<PermissionFormDto, UUID> permissionStore, Labels labels) {
        Objects.requireNonNull(roleStore, "roleStore");
        Objects.requireNonNull(permissionStore, "permissionStore");
        Labels effective = Labels.defaults().merge(labels);
        return ResourcePanels.auto(RoleFormDto.class)
                .withStore(roleStore)
                .actionProvider(IamUiActions.rolesCrudProvider())
                .override("name", cfg -> cfg.label(effective.roleName()))
                .override("permissions", cfg -> cfg.label(effective.rolePermissions()))
                .rowAction(IamUiActions.assignPermissionsAction())
                .build();
    }

    public static GridFormsResourceView<PermissionFormDto> permissions(CrudStore<PermissionFormDto, UUID> permissionStore) {
        return permissions(permissionStore, Labels.defaults());
    }

    public static GridFormsResourceView<PermissionFormDto> permissions(CrudStore<PermissionFormDto, UUID> permissionStore, Labels labels) {
        Objects.requireNonNull(permissionStore, "permissionStore");
        Labels effective = Labels.defaults().merge(labels);
        return ResourcePanels.auto(PermissionFormDto.class)
                .withStore(permissionStore)
                .actionProvider(IamUiActions.permissionsCrudProvider())
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
