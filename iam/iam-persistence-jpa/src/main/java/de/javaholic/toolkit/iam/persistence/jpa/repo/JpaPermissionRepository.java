package de.javaholic.toolkit.iam.persistence.jpa.repo;

import de.javaholic.toolkit.iam.persistence.jpa.entity.JpaPermissionEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPermissionRepository extends JpaRepository<JpaPermissionEntity, UUID> {

    Optional<JpaPermissionEntity> findByCode(String code);
}
