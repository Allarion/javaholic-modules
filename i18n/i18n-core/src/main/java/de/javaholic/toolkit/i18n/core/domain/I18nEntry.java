package de.javaholic.toolkit.i18n.core.domain;

import java.util.UUID;

public class I18nEntry {

    private UUID id;
    private String key;
    private String locale;
    private String value;
    private Long version;

    public I18nEntry() {
    }

    public I18nEntry(UUID id, String key, String locale, String value, Long version) {
        this.id = id;
        this.key = key;
        this.locale = locale;
        this.value = value;
        this.version = version;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

