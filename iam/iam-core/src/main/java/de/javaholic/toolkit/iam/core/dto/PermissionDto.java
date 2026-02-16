package de.javaholic.toolkit.iam.core.dto;

import de.javaholic.toolkit.ui.annotations.UIRequired;
import de.javaholic.toolkit.ui.annotations.UiLabel;

public class PermissionDto {

    @UIRequired
    @UiLabel("permission.name")
    private String code;

    public PermissionDto() {
    }

    public PermissionDto(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
