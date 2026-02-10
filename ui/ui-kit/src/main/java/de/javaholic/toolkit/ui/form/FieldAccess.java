package de.javaholic.toolkit.ui.form;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

final class FieldAccess {

    private final Field field;
    private final AnnotatedElement annotations;

    private FieldAccess(Field field, AnnotatedElement annotations) {
        this.field = field;
        this.annotations = annotations;
        this.field.setAccessible(true);
    }

    static List<FieldAccess> forType(Class<?> type) {
        if (type.isRecord()) {
            return fromRecord(type);
        }
        return fromDeclaredFields(type);
    }

    String name() {
        return field.getName();
    }

    Class<?> type() {
        return field.getType();
    }

    AnnotatedElement annotations() {
        return annotations;
    }

    Object get(Object bean) {
        try {
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    void set(Object bean, Object value) {
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<FieldAccess> fromDeclaredFields(Class<?> type) {
        List<FieldAccess> fields = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            if (field.isSynthetic() || java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            fields.add(new FieldAccess(field, field));
        }
        return fields;
    }

    private static List<FieldAccess> fromRecord(Class<?> type) {
        List<FieldAccess> fields = new ArrayList<>();
        for (RecordComponent component : type.getRecordComponents()) {
            try {
                Field field = type.getDeclaredField(component.getName());
                fields.add(new FieldAccess(field, component));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return fields;
    }
}
