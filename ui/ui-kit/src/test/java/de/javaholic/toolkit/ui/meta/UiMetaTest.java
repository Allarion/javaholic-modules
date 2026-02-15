package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.ui.annotations.UiHidden;
import de.javaholic.toolkit.ui.annotations.UiLabel;
import de.javaholic.toolkit.ui.annotations.UiOrder;
import de.javaholic.toolkit.ui.annotations.UiReadOnly;
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

    static class AnnotatedEntity {
        @UiHidden
        private String secret;

        @UiLabel("user.email.label")
        private String email;

        private String displayName;

        @UiReadOnly
        private String externalId;

        @UiOrder(10)
        public String getDisplayName() {
            return displayName;
        }
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

    @Test
    void uiHiddenAnnotationHidesProperty() {
        UiMeta<AnnotatedEntity> uiMeta = UiInspector.inspect(AnnotatedEntity.class);

        UiProperty<AnnotatedEntity> secret = uiMeta.properties()
                .filter(p -> p.name().equals("secret"))
                .findFirst()
                .orElseThrow();

        assertThat(secret.isVisible()).isFalse();
    }

    @Test
    void uiLabelAnnotationSetsLabelKey() {
        UiMeta<AnnotatedEntity> uiMeta = UiInspector.inspect(AnnotatedEntity.class);

        UiProperty<AnnotatedEntity> email = uiMeta.properties()
                .filter(p -> p.name().equals("email"))
                .findFirst()
                .orElseThrow();

        assertThat(email.labelKey()).isEqualTo("user.email.label");
    }

    @Test
    void uiOrderAnnotationSetsOrder() {
        UiMeta<AnnotatedEntity> uiMeta = UiInspector.inspect(AnnotatedEntity.class);

        UiProperty<AnnotatedEntity> displayName = uiMeta.properties()
                .filter(p -> p.name().equals("displayName"))
                .findFirst()
                .orElseThrow();

        assertThat(displayName.order()).isEqualTo(10);
    }

    @Test
    void uiReadOnlyAnnotationSetsReadOnlyFlag() {
        UiMeta<AnnotatedEntity> uiMeta = UiInspector.inspect(AnnotatedEntity.class);

        UiProperty<AnnotatedEntity> externalId = uiMeta.properties()
                .filter(p -> p.name().equals("externalId"))
                .findFirst()
                .orElseThrow();

        assertThat(externalId.isReadOnly()).isTrue();
    }
}

