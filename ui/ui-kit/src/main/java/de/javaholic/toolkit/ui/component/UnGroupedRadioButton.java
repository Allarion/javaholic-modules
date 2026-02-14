package de.javaholic.toolkit.ui.component;

import com.vaadin.flow.component.radiobutton.RadioButtonGroup;

import java.util.List;

public class UnGroupedRadioButton extends RadioButtonGroup<String> {

    public UnGroupedRadioButton() {
        this.setItems(List.of(""));
    }

    public void setChecked(final boolean checked) {
        super.setValue(checked ? "" : null);
    }
}