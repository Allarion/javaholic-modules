package de.javaholic.toolkit.iam.persistence.jpa.repo;

import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaRoleEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRoleRepository extends JpaRepository<JpaRoleEntity, UUID> {

    Optional<JpaRoleEntity> findByName(String name);
}
