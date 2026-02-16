package de.javaholic.toolkit.iam.core.dto;

import de.javaholic.toolkit.ui.annotations.UIRequired;
import de.javaholic.toolkit.ui.annotations.UiLabel;

import java.util.Set;

public class RoleDto {

    @UIRequired
    @UiLabel("role.name")
    private String name;

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
