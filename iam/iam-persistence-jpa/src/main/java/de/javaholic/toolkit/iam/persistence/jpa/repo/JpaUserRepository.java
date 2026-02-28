package de.javaholic.toolkit.iam.persistence.jpa.repo;

import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaUserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

    Optional<JpaUserEntity> findByIdentifier(String identifier);
}
