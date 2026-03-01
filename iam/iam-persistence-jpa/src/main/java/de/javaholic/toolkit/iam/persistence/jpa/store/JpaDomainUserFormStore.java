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
// TODO: add permissions to JpaCrudStore - no! Add SERVICE layer, add permission there!
    private final JpaUserMapper mapper;

    public JpaDomainUserFormStore(JpaUserRepository repo, JpaUserMapper mapper) {
        super(repo, mapper);
        this.mapper = mapper;
    }

    // TODO: test: is this reachable from UI?
    @Override
    public Optional<User> findByIdentifier(String identifier) {
        return repository.findByIdentifier(identifier)
                .map(mapper::toDomain);
    }
}
