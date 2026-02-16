package de.javaholic.toolkit.iam.core.dto;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.domain.UserStatus;
import de.javaholic.toolkit.ui.annotations.UIRequired;
import de.javaholic.toolkit.ui.annotations.UiLabel;

import java.util.Set;
import java.util.UUID;

public class UserDto {

    private UUID id;

    @UIRequired
    @UiLabel("form.user.email.label")
    private String username;

    @UIRequired
    @UiLabel("form.user.active.label")
    private UserStatus status;

    private Set<Role> roles;

    public UserDto() {
    }

    public UserDto(UUID id, String username, UserStatus status, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
