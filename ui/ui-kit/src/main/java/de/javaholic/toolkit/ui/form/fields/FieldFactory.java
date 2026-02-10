package de.javaholic.toolkit.ui.form.fields;

import com.vaadin.flow.component.Component;

@FunctionalInterface
public interface FieldFactory {
    Component create(FieldContext ctx);
}
