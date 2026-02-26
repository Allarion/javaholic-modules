package de.javaholic.toolkit.iam.dto;

import de.javaholic.toolkit.ui.annotations.UIRequired;
import de.javaholic.toolkit.ui.annotations.UiLabel;

public class PermissionFormDto {

    @UIRequired
    @UiLabel("form.permission.code.label")
    private String code;

    public PermissionFormDto() {
    }

    public PermissionFormDto(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

