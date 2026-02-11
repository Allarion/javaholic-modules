package de.javaholic.toolkit.ui.form.fields;

import com.vaadin.flow.component.HasValue;

@FunctionalInterface
public interface FieldFactory {
    HasValue<?, ?> create(FieldContext ctx);
}
