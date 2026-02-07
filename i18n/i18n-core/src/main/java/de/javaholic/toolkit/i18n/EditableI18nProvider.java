package de.javaholic.toolkit.i18n;

public interface EditableI18nProvider extends I18nProvider {
    void put(String key, String value);
}
