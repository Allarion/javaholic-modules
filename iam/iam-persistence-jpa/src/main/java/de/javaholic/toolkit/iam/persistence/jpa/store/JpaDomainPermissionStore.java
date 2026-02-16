package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaPermissionEntity;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaPermissionMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaPermissionRepository;
import de.javaholic.toolkit.persistence.springdata.store.AbstractJpaDomainCrudStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
public class JpaDomainPermissionStore extends AbstractJpaDomainCrudStore<Permission,UUID, JpaPermissionEntity, JpaPermissionRepository> implements PermissionStore {

    private final JpaPermissionMapper mapper;

    public JpaDomainPermissionStore(JpaPermissionRepository repository, JpaPermissionMapper mapper) {
        super(repository, mapper);
        this.mapper = mapper;
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        return repository.findByCode(code).map(mapper::toDomain);
    }
}
