package de.javaholic.toolkit.ui.crud;

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
class CrudPanelSmokeTest {

    @Mock
    private CrudStore<User, Long> store;

    @Mock
    private CrudStore<NoDefaultConstructorUser, Long> storeWithoutDefaultCtor;

    @Test
    void refreshLoadsItemsFromStore() {
        when(store.findAll()).thenReturn(List.of(new User()));

        CrudPanel<User> view = CrudPanel.of(User.class, store);

        assertThatCode(view::refresh).doesNotThrowAnyException();
        verify(store, atLeastOnce()).findAll();
    }

    @Test
    void saveAndRefreshDelegatesToStore() {
        when(store.findAll()).thenReturn(List.of());
        CrudPanel<User> view = CrudPanel.of(User.class, store);
        User user = new User();

        view.saveAndRefresh(user);

        verify(store).save(user);
        verify(store, atLeastOnce()).findAll();
    }

    @Test
    void deleteAndRefreshDelegatesToStore() {
        when(store.findAll()).thenReturn(List.of());
        CrudPanel<User> view = CrudPanel.of(User.class, store);
        User user = new User();

        view.deleteAndRefresh(user);

        verify(store).delete(user);
        verify(store, atLeastOnce()).findAll();
    }

    @Test
    void createFailsFastWithoutNoArgsConstructor() {
        when(storeWithoutDefaultCtor.findAll()).thenReturn(List.of());
        ExposedCrudPanel<NoDefaultConstructorUser> view =
                new ExposedCrudPanel<>(NoDefaultConstructorUser.class, storeWithoutDefaultCtor);

        assertThatThrownBy(view::triggerCreate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no-args constructor");
    }
    private static class ExposedCrudPanel<T> extends CrudPanel<T> {
            private ExposedCrudPanel(Class<T> type, CrudStore<T, ?> store) {
            super(type, store);
        }
        private void triggerCreate() {
            onCreate();
        }
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
