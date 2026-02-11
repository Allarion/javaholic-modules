package de.javaholic.toolkit.introspection;

import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BeanIntrospectorTest {

    static class Pojo {
        @Id
        private Long id;
        @Version
        private int version;
        @NotBlank
        private String name;
        @NotNull
        private String required;
    }

    record Rec(@Id Long id, @Version long version, @NotBlank String name) {
    }

    static class NoId {
        private String value;
    }

    static class SeveralId {
        @Id
        private long id1;
        @Id
        private long id2;
    }

    @Test
    void inspectPojoPropertiesAndIdVersion() {
        BeanMeta<Pojo> meta = BeanIntrospector.inspect(Pojo.class);

        List<BeanProperty> props = meta.properties();
        assertEquals(4, props.size());
        assertEquals("id", props.get(0).name());
        assertEquals("version", props.get(1).name());

        assertTrue(meta.idProperty().isPresent());
        assertEquals("id", meta.idProperty().get().name());

        assertTrue(meta.versionProperty().isPresent());
        assertEquals("version", meta.versionProperty().get().name());

        assertTrue(props.get(2).definition().isAnnotationPresent(NotBlank.class));
        assertTrue(props.get(3).definition().isAnnotationPresent(NotNull.class));
    }

    @Test
    void inspectRecordProperties() {
        BeanMeta<Rec> meta = BeanIntrospector.inspect(Rec.class);
        List<BeanProperty> props = meta.properties();

        assertEquals(3, props.size());
        assertEquals("id", props.get(0).name());
        assertTrue(meta.idProperty().isPresent());
        assertTrue(meta.versionProperty().isPresent());
    }

    @Test
    void inspectWithoutId() {
        BeanMeta<NoId> meta = BeanIntrospector.inspect(NoId.class);
        assertFalse(meta.idProperty().isPresent());
    }

    @Test
    void inspectWithSeveralIds() {
        assertThrows(IllegalStateException.class, () -> {
            BeanMeta<SeveralId> meta = BeanIntrospector.inspect(SeveralId.class);
        });
    }
}
