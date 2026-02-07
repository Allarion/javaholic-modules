package de.javaholic.toolkit.iam.persistence.jpa.store;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.iam.persistence.jpa.mapper.JpaUserMapper;
import de.javaholic.toolkit.iam.persistence.jpa.repo.JpaUserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class JpaUserStore implements UserStore {

    private final JpaUserRepository userRepository;
    private final JpaUserMapper userMapper;

    public JpaUserStore(JpaUserRepository userRepository, JpaUserMapper userMapper) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
        this.userMapper = Objects.requireNonNull(userMapper, "userMapper");
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll()
            .stream()
            .map(userMapper::toDomain)
            .collect(Collectors.toList());
    }
}
