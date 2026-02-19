package de.javaholic.toolkit.i18n.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.util.UUID;

@Entity
@Table(
    name = "i18n_entry",
    uniqueConstraints = @UniqueConstraint(name = "uk_i18n_key_locale", columnNames = {"key", "locale"})
)
public class JpaI18nEntry {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "translation_key", nullable = false)
    private String key;

    @Column(name = "locale", nullable = false)
    private String locale;

    @Column(name = "translation_value", nullable = false)
    private String value;

    @Version
    @Column(name = "version")
    private Long version;

    public JpaI18nEntry() {
    }

    public JpaI18nEntry(UUID id, String key, String locale, String value, Long version) {
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
