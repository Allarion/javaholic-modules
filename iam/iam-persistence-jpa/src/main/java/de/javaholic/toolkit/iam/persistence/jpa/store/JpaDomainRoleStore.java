package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaRoleEntity;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaRoleMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaRoleRepository;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import de.javaholic.toolkit.persistence.springdata.store.JpaDomainCrudStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class JpaDomainRoleStore extends JpaDomainCrudStore<Role, UUID, JpaRoleEntity, JpaRoleRepository> implements RoleStore {

    private final JpaRoleMapper mapper;
    public JpaDomainRoleStore(JpaRoleRepository roleRepository, JpaRoleMapper roleMapper) {
        super(roleRepository, roleMapper);
        this.mapper = Objects.requireNonNull(roleMapper, "roleMapper");
    }


    @Override
    public Optional<Role> findByName(String name) {
        return repository.findByName(name)
                .map(mapper::toDomain);
    }
}
