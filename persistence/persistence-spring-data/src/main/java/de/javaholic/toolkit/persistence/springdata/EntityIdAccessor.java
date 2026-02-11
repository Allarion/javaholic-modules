package de.javaholic.toolkit.persistence.springdata;

import de.javaholic.toolkit.persistence.core.FieldAccess;
import jakarta.persistence.Id;

import java.util.List;

final class EntityIdAccessor<T> {

    private final FieldAccess idField;

    EntityIdAccessor(Class<T> domainType) {
        List<FieldAccess> fields = FieldAccess.forType(domainType);
        FieldAccess match = null;
        for (FieldAccess field : fields) {
            if (field.annotations().isAnnotationPresent(Id.class)) {
                if (match != null) {
                    throw new IllegalStateException(
                            "Multiple @Id fields found on " + domainType.getName()
                    );
                }
                match = field;
            }
        }
        if (match == null) {
            throw new IllegalStateException(
                    "No @Id field found on " + domainType.getName()
            );
        }
        this.idField = match;
    }

    Object getId(T entity) {
        return idField.get(entity);
    }
}
