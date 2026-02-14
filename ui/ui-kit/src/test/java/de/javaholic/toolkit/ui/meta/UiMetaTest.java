package de.javaholic.toolkit.ui.meta;

import jakarta.persistence.Id;
import jakarta.persistence.Version;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UiMetaTest {

    static class Entity {
        @Id
        private long id;
        private String name;
        @Version
        private long version;
    }

    @Test
    void hidesIdAndVersionByDefault() {
        UiMeta<Entity> uiMeta = UiInspector.inspect(Entity.class);

        Map<String, Boolean> visibility = uiMeta.properties()
                .collect(java.util.stream.Collectors.toMap(UiProperty::name, UiProperty::isVisible));

        assertThat(visibility).containsEntry("id", false);
        assertThat(visibility).containsEntry("name", true);
        assertThat(visibility).containsEntry("version", false);
    }
}
