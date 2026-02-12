package de.javaholic.toolkit.ui;

import de.javaholic.toolkit.introspection.BeanIntrospector;
import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BeanMetaPropertySmokeTest {

    static class Entity {
        @Id
        private long id;
        private String name;
        private int count;
        @Version
        private long version;
    }

    @Test
    void getAndSetValuesForStringAndPrimitiveFields() {
        BeanMeta<Entity> meta = BeanIntrospector.inspect(Entity.class);
        BeanProperty name = meta.properties().stream()
                .filter(p -> p.name().equals("name"))
                .findFirst()
                .orElseThrow();
        BeanProperty count = meta.properties().stream()
                .filter(p -> p.name().equals("count"))
                .findFirst()
                .orElseThrow();

        Entity entity = new Entity();
        meta.setValue(name, entity, "Item");
        meta.setValue(count, entity, 4);

        assertThat(meta.getValue(name, entity)).isEqualTo("Item");
        assertThat(meta.getValue(count, entity)).isEqualTo(4);
        assertThat(meta.idProperty()).isPresent();
        assertThat(meta.versionProperty()).isPresent();
    }
}
