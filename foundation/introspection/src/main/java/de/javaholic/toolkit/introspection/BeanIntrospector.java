package de.javaholic.toolkit.introspection;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.*;

/**
 * Reflection-based factory that builds {@link BeanMeta}.
 *
 * <p>Responsibility: inspect fields/record components, resolve {@code @Id}/{@code @Version},
 * and return a normalized technical metadata model.</p>
 *
 * <p>Must not do: infer UI defaults, labels, ordering, or component choices.</p>
 *
 * <p>Architecture fit: boundary between raw Java reflection and the toolkit metadata model.
 * UI code should consume {@code UiInspector}/{@code UiMeta}, not call this from rendering builders.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * BeanMeta<User> meta = BeanIntrospector.inspect(User.class);
 * meta.idProperty().ifPresent(id -> System.out.println(id.name()));
 * }</pre>
 *
 * <p>Concept: this class extracts only technical structure (properties, id/version markers)
 * and intentionally does not decide any UI semantics such as labels, visibility, or ordering.</p>
 */
public final class BeanIntrospector {

    private BeanIntrospector() {
    }

    /**
     * Builds technical metadata for the given bean/record type.
     *
     * <p>Example:</p>
     * <pre>{@code
     * BeanMeta<User> meta = BeanIntrospector.inspect(User.class);
     * }</pre>
     *
     * <p>Record support example:</p>
     * <pre>{@code
     * record UserRow(@Id UUID id, String username) {}
     * BeanMeta<UserRow> meta = BeanIntrospector.inspect(UserRow.class);
     * }</pre>
     */
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

    /**
     * Detects id marker on fields/record components.
     *
     * <p>Concept note: currently this relies on JPA annotations. Long-term, this should move to an
     * adapter strategy so foundation stays persistence-agnostic.</p>
     * // TODO: remove JPA depencency. also Compare with technicalField interpretation.
     */
    private static boolean isId(AnnotatedElement element) {
        return element.isAnnotationPresent(jakarta.persistence.Id.class);
    }

    /**
     * Detects optimistic lock/version marker on fields/record components.
     */
    private static boolean isVersion(AnnotatedElement element) {
        return element.isAnnotationPresent(jakarta.persistence.Version.class);
    }
}
