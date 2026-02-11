package de.javaholic.toolkit.persistence.core;

import de.javaholic.toolkit.introspection.BeanIntrospector;
import de.javaholic.toolkit.introspection.BeanMeta;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityIdAccessorTest {

    static class Entity {
        @Id
        private Long id;
        @Version
        private Integer version;

        Entity(Long id, Integer version) {
            this.id = id;
            this.version = version;
        }
    }

    @Test
    void getIdAndVersion() {
        BeanMeta<Entity> meta = BeanIntrospector.inspect(Entity.class);
        EntityIdAccessor<Entity> accessor = new EntityIdAccessor<>(meta);

        Entity entity = new Entity(12L, 3);

        assertEquals(12L, accessor.getId(entity));
        Optional<Object> version = accessor.getVersion(entity);
        assertTrue(version.isPresent());
        assertEquals(3, version.get());
    }
}
