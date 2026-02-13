package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaUserEntity;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaUserMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaUserRepository;
import de.javaholic.toolkit.persistence.springdata.store.AbstractJpaCrudStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
public class JpaUserStore extends AbstractJpaCrudStore<User, UUID, JpaUserEntity, JpaUserRepository> implements UserStore {

    private final JpaUserMapper mapper;

    public JpaUserStore(JpaUserRepository repo, JpaUserMapper mapper) {
        super(repo);
        this.mapper = mapper;
    }

    @Override
    protected User toDomain(JpaUserEntity entity) {
        return mapper.toDomain(entity);
    }

    @Override
    protected JpaUserEntity toJpa(User domain) {
        return mapper.toJpa(domain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDomain);
    }
}
