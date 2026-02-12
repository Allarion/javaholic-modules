package de.javaholic.toolkit.persistence.springdata;

import de.javaholic.toolkit.persistence.core.CrudStore;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrudStoreFactorySmokeTest {

    static class User {
        @Id
        private UUID id;
        private String name;
    }

    interface UserRepository extends JpaRepository<User, UUID> {}

    @Mock
    UserRepository repository;

    @Test
    void createsCrudStoreFromApplicationContext() {
        when(repository.findAll()).thenReturn(List.of());

        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("userRepo", repository);
        context.refresh();

        SpringDataCrudStoreFactory factory = new SpringDataCrudStoreFactory(context);
        CrudStore<User, ?> store = factory.forType(User.class);

        assertThat(store).isNotNull();
        assertThat(store.findAll()).isEmpty();
    }
}

