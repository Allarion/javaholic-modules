package de.javaholic.toolkit.i18n.dto;

import de.javaholic.toolkit.ui.annotations.UIRequired;
import de.javaholic.toolkit.ui.annotations.UiOrder;

public class I18nEntryDto {

    @UiOrder(0)
    private String key;

    @UiOrder(1)
    private String locale;

    @UiOrder(2)
    @UIRequired
    private String value;

    public I18nEntryDto() {
    }

    public I18nEntryDto(String key, String locale, String value) {
        this.key = key;
        this.locale = locale;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

