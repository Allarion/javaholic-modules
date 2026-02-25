package de.javaholic.toolkit.ui.form.fields;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import de.javaholic.toolkit.introspection.BeanIntrospector;
import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;
import de.javaholic.toolkit.introspection.BeanPropertyTypes;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionFieldTest {

    static class Demo {
        private Set<String> tags;

        public Set<String> getTags() {
            return tags;
        }

        public void setTags(Set<String> tags) {
            this.tags = tags;
        }
    }

    @Test
    void createsMultiSelectAndDetectsStringElementType() {
        BeanMeta<Demo> meta = BeanIntrospector.inspect(Demo.class);
        BeanProperty<Demo, ?> property = meta.properties().stream()
                .filter(p -> p.name().equals("tags"))
                .findFirst()
                .orElseThrow();

        Class<?> elementType = BeanPropertyTypes.resolveCollectionElementType(Demo.class, property);

        FieldContext ctx = new FieldContext(Demo.class, property.name(), property.type(), elementType, property.definition());
        HasValue<?, ?> field = new FieldRegistry().create(ctx);

        assertThat(field).isInstanceOf(MultiSelectComboBox.class);
        assertThat(ctx.getElementType()).isEqualTo(String.class);
    }
}
