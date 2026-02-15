package de.javaholic.toolkit.ui.contract;

import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.crud.CrudPanel;
import de.javaholic.toolkit.ui.crud.CrudPanels;
import de.javaholic.toolkit.ui.meta.UiProperty;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CrudFluentApiContractTest {

    @Test
    void manualCrudBuilderChainsAndBuildsCrudPanel() {
        StubCrudStore<UserDto> store = new StubCrudStore<>();
        CrudPanels.CrudBuilder<UserDto> builder = CrudPanels.of(UserDto.class);

        CrudPanels.CrudBuilder<UserDto> chained = builder
                .withStore(store)
                .withTextResolver(key -> key)
                .withPropertyFilter(UiProperty::isVisible);

        CrudPanel<UserDto> result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(CrudPanel.class);
    }

    @Test
    void autoCrudBuilderChainsAndBuildsCrudPanel() {
        StubCrudStore<UserDto> store = new StubCrudStore<>();
        CrudPanels.AutoCrudBuilder<UserDto> builder = CrudPanels.auto(UserDto.class);

        CrudPanels.AutoCrudBuilder<UserDto> chained = builder
                .withStore(store)
                .withTextResolver(key -> key)
                .withPropertyFilter(UiProperty::isVisible)
                .override("email", property -> property.label("user.email.label"));

        CrudPanel<UserDto> result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(CrudPanel.class);
    }

    @Test
    void crudPanelExposesNoFluentConfigurationMethods() {
        List<Method> fluentNamedMethods = Arrays.stream(CrudPanel.class.getDeclaredMethods())
                .filter(method -> method.getName().startsWith("with")
                        || method.getName().startsWith("override")
                        || method.getName().equals("build"))
                .toList();
        List<Method> fluentReturnMethods = Arrays.stream(CrudPanel.class.getDeclaredMethods())
                .filter(method -> method.getReturnType().equals(CrudPanel.class))
                .toList();

        assertThat(fluentNamedMethods).isEmpty();
        assertThat(fluentReturnMethods).isEmpty();
    }

    @Test
    void crudBuilderMethodReturnTypesStayStable() throws NoSuchMethodException {
        assertThat(CrudPanels.CrudBuilder.class.getMethod("withStore", CrudStore.class).getReturnType())
                .isEqualTo(CrudPanels.CrudBuilder.class);
        assertThat(CrudPanels.CrudBuilder.class.getMethod("build").getReturnType())
                .isEqualTo(CrudPanel.class);

        assertThat(CrudPanels.AutoCrudBuilder.class.getMethod("withStore", CrudStore.class).getReturnType())
                .isEqualTo(CrudPanels.AutoCrudBuilder.class);
        assertThat(CrudPanels.AutoCrudBuilder.class.getMethod("override", String.class, java.util.function.Consumer.class).getReturnType())
                .isEqualTo(CrudPanels.AutoCrudBuilder.class);
        assertThat(CrudPanels.AutoCrudBuilder.class.getMethod("build").getReturnType())
                .isEqualTo(CrudPanel.class);
    }

    static final class StubCrudStore<T> implements CrudStore<T, Long> {
        @Override
        public List<T> findAll() {
            return List.of();
        }

        @Override
        public Optional<T> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public T save(T entity) {
            return entity;
        }

        @Override
        public void delete(T entity) {
            // no-op contract stub
        }
    }

    static class UserDto {
        private String email;
    }
}
