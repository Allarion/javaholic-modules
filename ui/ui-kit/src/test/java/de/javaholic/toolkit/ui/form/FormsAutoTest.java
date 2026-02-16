package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.textfield.TextField;
import de.javaholic.toolkit.ui.annotations.UiHidden;
import de.javaholic.toolkit.ui.annotations.UiLabel;
import de.javaholic.toolkit.ui.annotations.UiOrder;
import de.javaholic.toolkit.ui.annotations.UiReadOnly;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FormsAutoTest {

    static class Entity {
        @Id
        private long id;
        private String name;
        @Version
        private long version;
        private String email;
    }

    static class AnnotatedEntity {
        @UiOrder(20)
        @UiLabel("user.email.label")
        private String email;

        @UiOrder(10)
        @UiReadOnly
        private String externalId;

        @UiHidden
        private String internalCode;
    }

    @Test
    void autoHidesIdAndVersionByDefault() {
        Forms.Form<Entity> form = Forms.auto(Entity.class).build();

        assertThat(form.field("id")).isEmpty();
        assertThat(form.field("version")).isEmpty();
        assertThat(form.field("name")).isPresent();
        assertThat(form.field("email")).isPresent();
    }

    @Test
    void autoExcludeRemovesConfiguredProperties() {
        Forms.Form<Entity> form = Forms.auto(Entity.class)
                .exclude("email")
                .build();

        assertThat(form.field("name")).isPresent();
        assertThat(form.field("email")).isEmpty();
    }

    @Test
    void autoOverrideIsApplied() {
        Forms.Form<Entity> form = Forms.auto(Entity.class)
                .override("name", field -> ((TextField) field).setLabel("Custom Name"))
                .build();

        TextField field = (TextField) form.field("name").orElseThrow();
        assertThat(field.getLabel()).isEqualTo("Custom Name");
    }

    @Test
    void labelKeyIsResolvedToText() {
        Forms.Form<AnnotatedEntity> form = Forms.auto(AnnotatedEntity.class)
                .withTextResolver((key,locale) -> Optional.of("resolved:" + key))
                .build();

        TextField field = (TextField) form.field("email").orElseThrow();
        assertThat(field.getLabel()).isEqualTo("resolved:user.email.label");
    }

    @Test
    void readOnlyAnnotationAffectsFormField() {
        Forms.Form<AnnotatedEntity> form = Forms.auto(AnnotatedEntity.class).build();

        TextField field = (TextField) form.field("externalId").orElseThrow();
        assertThat(field.isReadOnly()).isTrue();
    }

    @Test
    void hiddenAnnotationRemovesAutoField() {
        Forms.Form<AnnotatedEntity> form = Forms.auto(AnnotatedEntity.class).build();

        assertThat(form.field("internalCode")).isEmpty();
    }

    @Test
    void orderAnnotationSortsFields() {
        Forms.Form<AnnotatedEntity> form = Forms.auto(AnnotatedEntity.class).build();

        TextField first = (TextField) form.field("externalId").orElseThrow();
        TextField second = (TextField) form.field("email").orElseThrow();
        assertThat(form.layout().getChildren().toList()).containsSubsequence(first, second);
    }
}

