package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.spi.RoleFormStore;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaRoleEntity;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaRoleMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaRoleRepository;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import de.javaholic.toolkit.persistence.springdata.store.JpaDomainCrudStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class JpaDomainRoleFormStore extends JpaDomainCrudStore<Role, UUID, JpaRoleEntity, JpaRoleRepository> implements RoleFormStore {

    private final JpaRoleMapper mapper;
    public JpaDomainRoleFormStore(JpaRoleRepository roleRepository, JpaRoleMapper roleMapper) {
        super(roleRepository, roleMapper);
        this.mapper = Objects.requireNonNull(roleMapper, "roleMapper");
    }


    @Override
    public Optional<Role> findByName(String name) {
        return repository.findByName(name)
                .map(mapper::toDomain);
    }
}
