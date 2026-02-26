package de.javaholic.toolkit.ui.resource;

import de.javaholic.toolkit.persistence.core.CrudStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourcePanelSmokeTest {

    @Mock
    private CrudStore<User, Long> store;

    @Mock
    private CrudStore<NoDefaultConstructorUser, Long> storeWithoutDefaultCtor;

    @Test
    void refreshLoadsItemsFromStore() {
        when(store.findAll()).thenReturn(List.of(new User()));

        ResourcePanel<User> view = ResourcePanels.of(User.class).withStore(store).build();

        assertThatCode(view::refresh).doesNotThrowAnyException();
        verify(store, atLeastOnce()).findAll();
    }

    @Test
    void saveAndRefreshDelegatesToStore() {
        when(store.findAll()).thenReturn(List.of());
        ResourcePanel<User> view = ResourcePanels.of(User.class).withStore(store).build();
        User user = new User();

        view.saveAndRefresh(user);

        verify(store).save(user);
        verify(store, atLeastOnce()).findAll();
    }

    @Test
    void deleteAndRefreshDelegatesToStore() {
        when(store.findAll()).thenReturn(List.of());
        ResourcePanel<User> view = ResourcePanels.of(User.class).withStore(store).build();
        User user = new User();

        view.deleteAndRefresh(user);

        verify(store).delete(user);
        verify(store, atLeastOnce()).findAll();
    }

    @Test
    void createFailsFastWithoutNoArgsConstructor() {
        when(storeWithoutDefaultCtor.findAll()).thenReturn(List.of());
        ResourcePanel<NoDefaultConstructorUser> view = ResourcePanels.of(NoDefaultConstructorUser.class)
                .withStore(storeWithoutDefaultCtor)
                .build();

        assertThatThrownBy(view::onCreate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no-args constructor");
    }

    static class User {
        private String name;
    }

    static class NoDefaultConstructorUser {
        private final String name;

        NoDefaultConstructorUser(String name) {
            this.name = name;
        }
    }
}

