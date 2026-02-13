package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaUserMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaUserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class JpaUserStore implements UserStore {

    private final JpaUserRepository repository;
    private final JpaUserMapper mapper;

    // TODO: Generell: feeling: @NotNull > Objects.requireNonNull
    public JpaUserStore(JpaUserRepository userRepository, JpaUserMapper userMapper) {
        this.repository = Objects.requireNonNull(userRepository, "userRepository");
        this.mapper = Objects.requireNonNull(userMapper, "userMapper");
    }

    @Override
    public List<User> findAll() {
        return repository.findAll()
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(UUID id) {
       return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
       return mapper.toDomain(repository.save(mapper.toJpa(user)));
    }

    @Override
    public void delete(User user) {
        repository.delete(mapper.toJpa(user));
    }
}
