package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaRoleEntity;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaRoleMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaRoleRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import de.javaholic.toolkit.persistence.springdata.store.AbstractJpaCrudStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class JpaRoleStore extends AbstractJpaCrudStore<Role, UUID, JpaRoleEntity, JpaRoleRepository> implements RoleStore {

    private final JpaRoleMapper mapper;
    // TODO: Generell: feeling: @NotNull > Objects.requireNonNull
    public JpaRoleStore(JpaRoleRepository roleRepository, JpaRoleMapper roleMapper) {
        super(roleRepository);
        this.mapper = Objects.requireNonNull(roleMapper, "roleMapper");
    }


    @Override
    public Optional<Role> findByName(String name) {
        return repository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    protected Role toDomain(JpaRoleEntity entity) {
        return mapper.toDomain(entity);
    }

    @Override
    protected JpaRoleEntity toJpa(Role domain) {
        return mapper.toJpa(domain);
    }
}
