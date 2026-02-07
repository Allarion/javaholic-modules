package de.javaholic.toolkit.i18n;

import java.util.function.Supplier;

public interface I18nProvider extends Supplier<I18n> {
    default boolean contains(String key) {
        return false;
    }
}