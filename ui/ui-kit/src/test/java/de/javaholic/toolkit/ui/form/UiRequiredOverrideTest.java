package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.textfield.TextField;
import de.javaholic.toolkit.ui.annotations.UIRequired;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UiRequiredOverrideTest {

    static class UiRequiredOnlyModel {
        @UIRequired
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    static class UiRequiredAndDomainModel {
        @UIRequired
        @NotBlank
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @Test
    void uiRequiredMakesFieldRequiredWithoutDomainNotNull() {
        Forms.Form<UiRequiredOnlyModel> form = Forms.of(UiRequiredOnlyModel.class).build();
        form.binder().setBean(new UiRequiredOnlyModel());

        TextField field = (TextField) form.field("code").orElseThrow();

        assertThat(field.isRequiredIndicatorVisible()).isTrue();
        assertThat(form.binder().validate().isOk()).isFalse();

        field.setValue("X");

        assertThat(form.binder().validate().isOk()).isTrue();
    }

    @Test
    void uiRequiredWithDomainConstraintProducesSingleFieldError() {
        Forms.Form<UiRequiredAndDomainModel> form = Forms.of(UiRequiredAndDomainModel.class).build();
        form.binder().setBean(new UiRequiredAndDomainModel());

        var status = form.binder().validate();

        assertThat(status.isOk()).isFalse();
        assertThat(status.getFieldValidationErrors()).hasSize(1);
    }
}
