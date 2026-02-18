package de.javaholic.toolkit.iam.dto.dto;

import de.javaholic.toolkit.ui.annotations.UIRequired;
import de.javaholic.toolkit.ui.annotations.UiLabel;

import java.util.Set;

public class RoleDto {

    @UIRequired
    @UiLabel("form.role.name.label")
    private String name;

    @UiLabel("form.role.permissions.label")
    private Set<PermissionDto> permissions;

    public RoleDto() {
    }

    public RoleDto(String name, Set<PermissionDto> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionDto> permissions) {
        this.permissions = permissions;
    }
}

