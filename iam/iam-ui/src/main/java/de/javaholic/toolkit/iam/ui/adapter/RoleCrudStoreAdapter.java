package de.javaholic.toolkit.iam.ui.adapter;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RoleCrudStoreAdapter implements CrudStore<Role, UUID> {
    private final RoleStore delegate;

    public RoleCrudStoreAdapter(RoleStore RoleStore) {
        this.delegate = RoleStore;
    }

    @Override
    public List<Role> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<Role> findById(UUID uuid) {
        return delegate.findById(uuid);
    }

    @Override
    public Role save(Role entity) {
        return delegate.save(entity);
    }

    @Override
    public void delete(Role entity) {
        delegate.delete(entity);
    }
}
