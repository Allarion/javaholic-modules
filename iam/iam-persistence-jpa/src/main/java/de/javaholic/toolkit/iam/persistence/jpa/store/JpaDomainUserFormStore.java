package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.spi.UserFormStore;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaUserEntity;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaUserMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaUserRepository;
import de.javaholic.toolkit.persistence.springdata.store.JpaDomainCrudStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
public class JpaDomainUserFormStore extends JpaDomainCrudStore<User, UUID, JpaUserEntity, JpaUserRepository> implements UserFormStore {

    private final JpaUserMapper mapper;

    public JpaDomainUserFormStore(JpaUserRepository repo, JpaUserMapper mapper) {
        super(repo, mapper);
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDomain);
    }
}
