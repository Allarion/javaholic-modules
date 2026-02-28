package de.javaholic.toolkit.iam.dto;

import de.javaholic.toolkit.iam.core.domain.UserStatus;
import de.javaholic.toolkit.ui.annotations.UIRequired;
import de.javaholic.toolkit.ui.annotations.UiLabel;

import java.util.Set;
import java.util.UUID;

public class UserFormDto {

    private UUID id;

    @UIRequired
    @UiLabel("form.user.identifier.label")
    private String identifier;

    @UiLabel("form.user.display-name.label")
    private String displayName;

    @UIRequired
    @UiLabel("form.user.status.label")
    private UserStatus status;

    @UiLabel("form.user.roles.label")
    private Set<RoleFormDto> roles;

    public UserFormDto() {
    }

    public UserFormDto(UUID id, String identifier, String displayName, UserStatus status, Set<RoleFormDto> roles) {
        this.id = id;
        this.identifier = identifier;
        this.displayName = displayName;
        this.status = status;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Set<RoleFormDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleFormDto> roles) {
        this.roles = roles;
    }
}

