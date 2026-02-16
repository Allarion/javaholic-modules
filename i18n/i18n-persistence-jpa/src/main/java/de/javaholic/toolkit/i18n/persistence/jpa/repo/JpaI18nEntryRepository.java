package de.javaholic.toolkit.i18n.persistence.jpa.repo;

import de.javaholic.toolkit.i18n.persistence.jpa.entity.JpaI18nEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaI18nEntryRepository extends JpaRepository<JpaI18nEntry, UUID> {

    Optional<JpaI18nEntry> findByKeyAndLocale(String key, String locale);

    List<JpaI18nEntry> findByKey(String key);
}
