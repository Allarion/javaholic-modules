package de.javaholic.toolkit.ui.form.fields;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.select.Select;
import de.javaholic.toolkit.ui.component.UuidField;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultMappingsTest {

    enum Status { ACTIVE, INACTIVE }

    static class Sample {
        private UUID id;
        private Status status;
    }

    @Test
    void defaultsCreateUuidAndEnumFieldsFromInputs() throws NoSuchFieldException {
        FieldRegistry registry = new FieldRegistry();

        HasValue<?, ?> uuid = registry.create(context("id", UUID.class));
        HasValue<?, ?> status = registry.create(context("status", Status.class));

        assertThat(uuid).isInstanceOf(UuidField.class);
        assertThat(status).isInstanceOf(Select.class);
    }

    private static FieldContext context(String property, Class<?> type) throws NoSuchFieldException {
        Field field = Sample.class.getDeclaredField(property);
        return new FieldContext(Sample.class, property, type, field);
    }
}
