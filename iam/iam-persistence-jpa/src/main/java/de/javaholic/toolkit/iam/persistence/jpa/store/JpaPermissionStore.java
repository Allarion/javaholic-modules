package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaPermissionEntity;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaPermissionMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaPermissionRepository;
import de.javaholic.toolkit.persistence.springdata.store.AbstractJpaCrudStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public class JpaPermissionStore extends AbstractJpaCrudStore<Permission,UUID, JpaPermissionEntity, JpaPermissionRepository> implements PermissionStore {

    private final JpaPermissionMapper mapper;

    public JpaPermissionStore(JpaPermissionRepository repository, JpaPermissionMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        return repository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    protected Permission toDomain(JpaPermissionEntity entity) {
        return mapper.toDomain(entity);
    }

    @Override
    protected JpaPermissionEntity toJpa(Permission domain) {
        return mapper.toJpa(domain);
    }
}
