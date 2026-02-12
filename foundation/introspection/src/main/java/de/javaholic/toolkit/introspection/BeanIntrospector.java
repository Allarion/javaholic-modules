package de.javaholic.toolkit.introspection;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.*;

public final class BeanIntrospector {

    private BeanIntrospector() {
    }

    public static <T> BeanMeta<T> inspect(Class<T> type) {

        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }

        List<BeanProperty<T,?>> properties = new ArrayList<>();
        Map<String, Field> accessors = new LinkedHashMap<>();

        BeanProperty<T,?> idProperty = null;
        BeanProperty<T, ?> versionProperty = null;

        if (type.isRecord()) {

            for (RecordComponent component : type.getRecordComponents()) {

                Field field = findField(type, component.getName());
                field.setAccessible(true);

                BeanProperty<T,?> prop = new BeanProperty(
                        component.getName(),
                        component.getType(),
                        component
                );

                properties.add(prop);
                accessors.put(prop.name(), field);

                if (isId(component) || isId(field)) {
                    if (idProperty != null) {
                        throw new IllegalStateException(
                                "Multiple @Id properties found on " + type.getName()
                        );
                    }
                    idProperty = prop;
                }

                if (isVersion(component) || isVersion(field)) {
                    versionProperty = prop;
                }
            }

        } else {

            for (Field field : type.getDeclaredFields()) {

                if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                BeanProperty prop = new BeanProperty(
                        field.getName(),
                        field.getType(),
                        field
                );

                properties.add(prop);
                accessors.put(prop.name(), field);

                if (isId(field)) {
                    if (idProperty != null) {
                        throw new IllegalStateException(
                                "Multiple @Id properties found on " + type.getName()
                        );
                    }
                    idProperty = prop;
                }

                if (isVersion(field)) {
                    versionProperty = prop;
                }
            }
        }

        return new BeanMeta<>(
                type,
                properties,
                accessors,
                idProperty,
                versionProperty
        );
    }


    private static Field findField(Class<?> type, String name) {
        try {
            return type.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isId(AnnotatedElement element) {
        return element.isAnnotationPresent(jakarta.persistence.Id.class);
    }

    private static boolean isVersion(AnnotatedElement element) {
        return element.isAnnotationPresent(jakarta.persistence.Version.class);
    }
}
