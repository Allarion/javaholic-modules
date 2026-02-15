package de.javaholic.toolkit.ui.crud;

import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.form.Forms;
import de.javaholic.toolkit.ui.meta.UiProperty;
import de.javaholic.toolkit.ui.text.DefaultTextResolver;
import de.javaholic.toolkit.ui.text.TextResolver;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Factory for staged CRUD panel creation.
 */
public final class CrudPanels {

    private CrudPanels() {
    }

    public static <T> TypeStage<T> of(Class<T> type) {
        return new Builder<>(type);
    }

    public interface TypeStage<T> {
        ConfigStage<T> withStore(CrudStore<T, ?> store);
    }

    public interface ConfigStage<T> {
        ConfigStage<T> withTextResolver(TextResolver resolver);

        ConfigStage<T> withPropertyFilter(Predicate<UiProperty<T>> filter);

        ConfigStage<T> withFormBuilderFactory(Supplier<Forms.FormBuilder<T>> formBuilderFactory);

        CrudPanel<T> build();
    }

    private static final class Builder<T> implements TypeStage<T>, ConfigStage<T> {
        private final Class<T> type;
        private CrudStore<T, ?> store;
        private TextResolver textResolver = new DefaultTextResolver();
        private Predicate<UiProperty<T>> propertyFilter = UiProperty::isVisible;
        private Supplier<Forms.Form<T>> formFactory;

        private Builder(Class<T> type) {
            this.type = Objects.requireNonNull(type, "type");
        }

        @Override
        public ConfigStage<T> withStore(CrudStore<T, ?> store) {
            this.store = Objects.requireNonNull(store, "store");
            return this;
        }

        @Override
        public ConfigStage<T> withTextResolver(TextResolver resolver) {
            this.textResolver = Objects.requireNonNull(resolver, "resolver");
            return this;
        }

        @Override
        public ConfigStage<T> withPropertyFilter(Predicate<UiProperty<T>> filter) {
            this.propertyFilter = Objects.requireNonNull(filter, "filter");
            return this;
        }

        @Override
        public ConfigStage<T> withFormBuilderFactory(Supplier<Forms.FormBuilder<T>> formBuilderFactory) {
            Objects.requireNonNull(formBuilderFactory, "formBuilderFactory");
            this.formFactory = () -> formBuilderFactory.get().build();
            return this;
        }

        @Override
        public CrudPanel<T> build() {
            return new CrudPanel<>(type, store, textResolver, propertyFilter, formFactory);
        }
    }
}
