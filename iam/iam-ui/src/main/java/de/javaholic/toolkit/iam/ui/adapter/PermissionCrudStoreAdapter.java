package de.javaholic.toolkit.iam.ui.adapter;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PermissionCrudStoreAdapter implements CrudStore<Permission, UUID> {
    private final PermissionStore delegate;

    public PermissionCrudStoreAdapter(PermissionStore PermissionStore) {
        this.delegate = PermissionStore;
    }

    @Override
    public List<Permission> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<Permission> findById(UUID uuid) {
        return delegate.findById(uuid);
    }

    @Override
    public Permission save(Permission entity) {
        return delegate.save(entity);
    }

    @Override
    public void delete(Permission entity) {
        delegate.delete(entity);
    }
}
