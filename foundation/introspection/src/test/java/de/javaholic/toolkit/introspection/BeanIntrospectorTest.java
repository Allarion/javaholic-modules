package de.javaholic.toolkit.introspection;

import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        List<BeanProperty<Pojo,?>> props = meta.properties();
        assertThat(props).hasSize(4);
        assertThat(props).extracting(BeanProperty::name)
                .containsExactly("id", "version", "name", "required");

        assertThat(meta.idProperty()).isPresent();
        assertThat(meta.idProperty().orElseThrow().name()).isEqualTo("id");

        assertThat(meta.versionProperty()).isPresent();
        assertThat(meta.versionProperty().orElseThrow().name()).isEqualTo("version");

        assertThat(props.get(2).definition().isAnnotationPresent(NotBlank.class)).isTrue();
        assertThat(props.get(3).definition().isAnnotationPresent(NotNull.class)).isTrue();
    }

    @Test
    void inspectRecordProperties() {
        BeanMeta<Rec> meta = BeanIntrospector.inspect(Rec.class);
        List<BeanProperty<Rec,?>> props = meta.properties();

        assertThat(props).hasSize(3);
        assertThat(props).extracting(BeanProperty::name)
                .containsExactly("id", "version", "name");
        assertThat(meta.idProperty()).isPresent();
        assertThat(meta.versionProperty()).isPresent();
    }

    @Test
    void inspectWithoutId() {
        BeanMeta<NoId> meta = BeanIntrospector.inspect(NoId.class);
        assertThat(meta.idProperty()).isEmpty();
    }

    @Test
    void inspectWithSeveralIds() {
        assertThatThrownBy(() -> BeanIntrospector.inspect(SeveralId.class))
                .isInstanceOf(IllegalStateException.class);
    }
}
