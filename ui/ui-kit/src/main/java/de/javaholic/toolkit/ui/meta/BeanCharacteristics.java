package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.ui.annotations.UiHidden;
import de.javaholic.toolkit.ui.annotations.UiPermission;
import jakarta.persistence.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;

final class BeanCharacteristics {

    final boolean isJpaEntity;
    final boolean hasUiAnnotations;

    private BeanCharacteristics(boolean isJpaEntity, boolean hasUiAnnotations) {
        this.isJpaEntity = isJpaEntity;
        this.hasUiAnnotations = hasUiAnnotations;
    }

    static BeanCharacteristics analyze(Class<?> type) {
        boolean jpaEntity = type.isAnnotationPresent(Entity.class);
        boolean uiAnnotations = hasUiAnnotations(type);
        return new BeanCharacteristics(jpaEntity, uiAnnotations);
    }

    private static boolean hasUiAnnotations(Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(UiHidden.class) || field.isAnnotationPresent(UiPermission.class)) {
                return true;
            }
        }
        if (type.isRecord()) {
            for (RecordComponent component : type.getRecordComponents()) {
                if (component.isAnnotationPresent(UiHidden.class) || component.isAnnotationPresent(UiPermission.class)) {
                    return true;
                }
            }
        }
        return false;
    }
}
