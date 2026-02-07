package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaRoleMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaRoleRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class JpaRoleStore implements RoleStore {

    private final JpaRoleRepository roleRepository;
    private final JpaRoleMapper roleMapper;

    public JpaRoleStore(JpaRoleRepository roleRepository, JpaRoleMapper roleMapper) {
        this.roleRepository = Objects.requireNonNull(roleRepository, "roleRepository");
        this.roleMapper = Objects.requireNonNull(roleMapper, "roleMapper");
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name)
            .map(roleMapper::toDomain);
    }
}
