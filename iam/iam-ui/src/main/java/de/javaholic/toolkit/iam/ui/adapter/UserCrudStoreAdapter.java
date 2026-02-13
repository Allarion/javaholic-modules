package de.javaholic.toolkit.iam.ui.adapter;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserCrudStoreAdapter implements CrudStore<User, UUID> {
    private final UserStore delegate;

    public UserCrudStoreAdapter(UserStore userStore) {
        this.delegate = userStore;
    }

    @Override
    public List<User> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<User> findById(UUID uuid) {
        return delegate.findById(uuid);
    }

    @Override
    public User save(User entity) {
        return delegate.save(entity);
    }

    @Override
    public void delete(User entity) {
        delegate.delete(entity);
    }
}
