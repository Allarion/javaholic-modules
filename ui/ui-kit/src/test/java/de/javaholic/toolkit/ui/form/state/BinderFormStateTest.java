package de.javaholic.toolkit.ui.form.state;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BinderFormStateTest {

    @Test
    void validReflectsBinderValidity() {
        Binder<FormBean> binder = new Binder<>(FormBean.class);
        FormBean bean = new FormBean();
        binder.setBean(bean);

        TextField name = new TextField();
        binder.forField(name)
                .asRequired("Name required")
                .bind(FormBean::getName, FormBean::setName);

        BinderFormState<FormBean> state = new BinderFormState<>(binder);

        assertThat(state.valid().get()).isFalse();

        name.setValue("Alice");

        assertThat(state.valid().get()).isTrue();
    }

    @Test
    void submittingBlocksCanSubmit() {
        Binder<FormBean> binder = new Binder<>(FormBean.class);
        FormBean bean = new FormBean();
        binder.setBean(bean);

        TextField name = new TextField();
        binder.forField(name)
                .asRequired("Name required")
                .bind(FormBean::getName, FormBean::setName);
        name.setValue("Alice");

        BinderFormState<FormBean> state = new BinderFormState<>(binder);

        assertThat(state.canSubmit().get()).isTrue();

        state.setSubmitting(true);

        assertThat(state.canSubmit().get()).isFalse();
    }

    private static class FormBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
