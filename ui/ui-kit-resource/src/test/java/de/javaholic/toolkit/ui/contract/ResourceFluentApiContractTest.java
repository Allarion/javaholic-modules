package de.javaholic.toolkit.ui.contract;

import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.ui.resource.ResourcePanel;
import de.javaholic.toolkit.ui.resource.ResourcePanels;
import de.javaholic.toolkit.ui.api.ResourceAction;
import de.javaholic.toolkit.ui.resource.action.ResourcePresets;
import de.javaholic.toolkit.ui.meta.UiProperty;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceFluentApiContractTest {

    @Test
    void manualCrudBuilderChainsAndBuildsResourcePanel() {
        StubCrudStore<UserDto> store = new StubCrudStore<>();
        ResourcePanels.CrudBuilder<UserDto> builder = ResourcePanels.of(UserDto.class);

        ResourcePanels.CrudBuilder<UserDto> chained = builder
                .withStore(store)
                .withTextResolver((key, locale) -> Optional.of(key))
                .withPropertyFilter(UiProperty::isVisible)
                .preset(ResourcePresets.full())
                .toolbarAction(ResourceAction.toolbar("t", () -> { }))
                .rowAction(ResourceAction.row("r", dto -> { }))
                .selectionAction(ResourceAction.selection("s", selection -> { }));

        ResourcePanel<UserDto> result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(ResourcePanel.class);
    }

    @Test
    void autoCrudBuilderChainsAndBuildsResourcePanel() {
        StubCrudStore<UserDto> store = new StubCrudStore<>();
        ResourcePanels.AutoCrudBuilder<UserDto> builder = ResourcePanels.auto(UserDto.class);

        ResourcePanels.AutoCrudBuilder<UserDto> chained = builder
                .withStore(store)
                .withTextResolver((key, locale) -> Optional.of(key))
                .withPropertyFilter(UiProperty::isVisible)
                .preset(ResourcePresets.full())
                .toolbarAction(ResourceAction.toolbar("t", () -> { }))
                .rowAction(ResourceAction.row("r", dto -> { }))
                .selectionAction(ResourceAction.selection("s", selection -> { }))
                .override("email", property -> property.label("user.email.label"));

        ResourcePanel<UserDto> result = chained.build();

        assertThat(chained).isSameAs(builder);
        assertThat(result).isInstanceOf(ResourcePanel.class);
    }

    @Test
    void crudPanelExposesNoFluentConfigurationMethods() {
        List<Method> fluentNamedMethods = Arrays.stream(ResourcePanel.class.getDeclaredMethods())
                .filter(method -> method.getName().startsWith("with")
                        || method.getName().startsWith("override")
                        || method.getName().equals("build"))
                .toList();
        List<Method> fluentReturnMethods = Arrays.stream(ResourcePanel.class.getDeclaredMethods())
                .filter(method -> method.getReturnType().equals(ResourcePanel.class))
                .toList();

        assertThat(fluentNamedMethods).isEmpty();
        assertThat(fluentReturnMethods).isEmpty();
    }

    @Test
    void crudBuilderMethodReturnTypesStayStable() throws NoSuchMethodException {
        assertThat(ResourcePanels.CrudBuilder.class.getMethod("withStore", CrudStore.class).getReturnType())
                .isEqualTo(ResourcePanels.CrudBuilder.class);
        assertThat(ResourcePanels.CrudBuilder.class.getMethod("preset", de.javaholic.toolkit.ui.resource.action.ResourcePreset.class).getReturnType())
                .isEqualTo(ResourcePanels.CrudBuilder.class);
        assertThat(ResourcePanels.CrudBuilder.class.getMethod("toolbarAction", ResourceAction.ToolbarAction.class).getReturnType())
                .isEqualTo(ResourcePanels.CrudBuilder.class);
        assertThat(ResourcePanels.CrudBuilder.class.getMethod("rowAction", ResourceAction.RowAction.class).getReturnType())
                .isEqualTo(ResourcePanels.CrudBuilder.class);
        assertThat(ResourcePanels.CrudBuilder.class.getMethod("selectionAction", ResourceAction.SelectionAction.class).getReturnType())
                .isEqualTo(ResourcePanels.CrudBuilder.class);
        assertThat(ResourcePanels.CrudBuilder.class.getMethod("build").getReturnType())
                .isEqualTo(ResourcePanel.class);

        assertThat(ResourcePanels.AutoCrudBuilder.class.getMethod("withStore", CrudStore.class).getReturnType())
                .isEqualTo(ResourcePanels.AutoCrudBuilder.class);
        assertThat(ResourcePanels.AutoCrudBuilder.class.getMethod("preset", de.javaholic.toolkit.ui.resource.action.ResourcePreset.class).getReturnType())
                .isEqualTo(ResourcePanels.AutoCrudBuilder.class);
        assertThat(ResourcePanels.AutoCrudBuilder.class.getMethod("toolbarAction", ResourceAction.ToolbarAction.class).getReturnType())
                .isEqualTo(ResourcePanels.AutoCrudBuilder.class);
        assertThat(ResourcePanels.AutoCrudBuilder.class.getMethod("rowAction", ResourceAction.RowAction.class).getReturnType())
                .isEqualTo(ResourcePanels.AutoCrudBuilder.class);
        assertThat(ResourcePanels.AutoCrudBuilder.class.getMethod("selectionAction", ResourceAction.SelectionAction.class).getReturnType())
                .isEqualTo(ResourcePanels.AutoCrudBuilder.class);
        assertThat(ResourcePanels.AutoCrudBuilder.class.getMethod("override", String.class, java.util.function.Consumer.class).getReturnType())
                .isEqualTo(ResourcePanels.AutoCrudBuilder.class);
        assertThat(ResourcePanels.AutoCrudBuilder.class.getMethod("build").getReturnType())
                .isEqualTo(ResourcePanel.class);
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


