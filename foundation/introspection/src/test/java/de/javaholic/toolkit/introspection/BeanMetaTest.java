package de.javaholic.toolkit.introspection;

import jakarta.persistence.Id;
import jakarta.persistence.Version;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BeanMetaTest {

    static class User {
        @Id
        private Long id;
        private String name;
        @Version
        private long version;
    }

    record UserRecord(@Id Long id, @Version long version, String name) {
    }

    // TODO: correct usage of generics
    @Test
    void getAndSetValueForPojo() {
        BeanMeta<User> meta = BeanIntrospector.inspect(User.class);
        BeanProperty nameProperty = meta.properties().stream()
                .filter(p -> p.name().equals("name"))
                .findFirst()
                .orElseThrow();

        User user = new User();
        meta.setValue(nameProperty, user, "Alice");

        assertThat(meta.getValue(nameProperty, user)).isEqualTo("Alice");
        assertThat(meta.idProperty()).isPresent();
        assertThat(meta.versionProperty()).isPresent();
    }

    @Test
    void getValueForRecord() {
        BeanMeta<UserRecord> meta = BeanIntrospector.inspect(UserRecord.class);
        BeanProperty<UserRecord, ?> nameProperty = meta.properties().stream()
                .filter(p -> p.name().equals("name"))
                .findFirst()
                .orElseThrow();

        UserRecord user = new UserRecord(7L, 3L, "Bob");

        assertThat(meta.getValue(nameProperty, user)).isEqualTo("Bob");
        assertThat(meta.idProperty()).isPresent();
        assertThat(meta.versionProperty()).isPresent();
    }
}
