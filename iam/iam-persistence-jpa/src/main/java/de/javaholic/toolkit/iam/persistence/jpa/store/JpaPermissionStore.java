package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaPermissionMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaPermissionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public class JpaPermissionStore implements PermissionStore {

    private final JpaPermissionRepository repository;
    private final JpaPermissionMapper mapper;

    public JpaPermissionStore(JpaPermissionRepository repository, JpaPermissionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        return repository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Permission save(Permission user) {
        return null;
    }

    @Override
    public void delete(Permission user) {

    }
}
