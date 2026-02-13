package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaRoleMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaRoleRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class JpaRoleStore implements RoleStore {

    private final JpaRoleRepository repository;
    private final JpaRoleMapper mapper;

    public JpaRoleStore(JpaRoleRepository roleRepository, JpaRoleMapper roleMapper) {
        this.repository = Objects.requireNonNull(roleRepository, "roleRepository");
        this.mapper = Objects.requireNonNull(roleMapper, "roleMapper");
    }

    @Override
    public Optional<Role> findByName(String name) {
        return repository.findByName(name)
            .map(mapper::toDomain);
    }

    public List<Role> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Role save(Role user) {
        return mapper.toDomain(repository.save(mapper.toJpa(user)));
    }

    @Override
    public void delete(Role user) {
        repository.delete(mapper.toJpa(user));
    }
}
