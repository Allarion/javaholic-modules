package de.javaholic.toolkit.iam.dto;

import de.javaholic.toolkit.ui.annotations.UIRequired;
import de.javaholic.toolkit.ui.annotations.UiLabel;

import java.util.Set;

public class RoleFormDto {

    @UIRequired
    @UiLabel("form.role.name.label")
    private String name;

    @UiLabel("form.role.permissions.label")
    private Set<PermissionFormDto> permissions;

    public RoleFormDto() {
    }

    public RoleFormDto(String name, Set<PermissionFormDto> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PermissionFormDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionFormDto> permissions) {
        this.permissions = permissions;
    }
}

