package de.javaholic.toolkit.ui.action;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActionBuilderDslTest {

    @Test
    void enabledWhenFieldTracksBooleanValue() {
        Checkbox checkbox = new Checkbox();
        checkbox.setValue(false);

        Actions.Action action = Actions.create()
                .label("X")
                .enabledWhen(checkbox)
                .build();

        assertThat(action.enabled().get()).isFalse();

        checkbox.setValue(true);

        assertThat(action.enabled().get()).isTrue();
    }

    @Test
    void multipleEnabledConditionsAreCombinedWithAnd() {
        Checkbox first = new Checkbox();
        Checkbox second = new Checkbox();

        Actions.Action action = Actions.create()
                .label("X")
                .enabledWhen(first)
                .enabledWhen(second)
                .build();

        assertThat(action.enabled().get()).isFalse();

        first.setValue(true);
        assertThat(action.enabled().get()).isFalse();

        second.setValue(true);
        assertThat(action.enabled().get()).isTrue();
    }

    @Test
    void enabledWhenBinderUsesIsValid() {
        Binder<FormBean> binder = new Binder<>(FormBean.class);
        FormBean bean = new FormBean();
        binder.setBean(bean);

        TextField name = new TextField();
        binder.forField(name)
                .asRequired("required")
                .bind(FormBean::getName, FormBean::setName);

        Actions.Action action = Actions.create()
                .label("Save")
                .enabledWhen(binder)
                .build();

        assertThat(action.enabled().get()).isFalse();

        name.setValue("ok");

        assertThat(action.enabled().get()).isTrue();
    }

    @Test
    void disabledAndHiddenFieldHelpersInvertCondition() {
        Checkbox toggle = new Checkbox();
        toggle.setValue(false);

        Actions.Action action = Actions.create()
                .label("X")
                .disabledWhen(toggle)
                .hiddenWhen(toggle)
                .build();

        assertThat(action.enabled().get()).isTrue();
        assertThat(action.visible().get()).isTrue();

        toggle.setValue(true);

        assertThat(action.enabled().get()).isFalse();
        assertThat(action.visible().get()).isFalse();
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
