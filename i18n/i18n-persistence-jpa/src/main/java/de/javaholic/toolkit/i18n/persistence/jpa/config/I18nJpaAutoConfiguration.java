package de.javaholic.toolkit.i18n.persistence.jpa.config;

import de.javaholic.toolkit.i18n.core.spi.I18nEntryStore;
import de.javaholic.toolkit.i18n.persistence.jpa.entity.JpaI18nEntry;
import de.javaholic.toolkit.i18n.persistence.jpa.mapper.JpaI18nEntryMapper;
import de.javaholic.toolkit.i18n.persistence.jpa.repo.JpaI18nEntryRepository;
import de.javaholic.toolkit.i18n.persistence.jpa.store.JpaI18nEntryStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@EnableJpaRepositories(basePackageClasses = JpaI18nEntryRepository.class)
@EntityScan(basePackageClasses = JpaI18nEntry.class)
public class I18nJpaAutoConfiguration {

    @Bean
    public JpaI18nEntryMapper jpaI18nEntryMapper() {
        return new JpaI18nEntryMapper();
    }

    @Bean
    @ConditionalOnMissingBean(I18nEntryStore.class)
    public JpaI18nEntryStore jpaI18nEntryStore(JpaI18nEntryRepository repository, JpaI18nEntryMapper mapper) {
        return new JpaI18nEntryStore(repository, mapper);
    }
}
