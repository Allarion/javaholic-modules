package de.javaholic.toolkit.i18n.persistence.jpa.config;

import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.i18n.core.spi.I18nEntryStore;
import de.javaholic.toolkit.i18n.dto.mapper.I18nEntryDtoMapper;
import de.javaholic.toolkit.i18n.dto.spi.I18nEntryDtoStore;
import de.javaholic.toolkit.i18n.dto.store.I18nEntryDtoCrudStore;
import de.javaholic.toolkit.i18n.persistence.jpa.entity.JpaI18nEntry;
import de.javaholic.toolkit.i18n.persistence.jpa.mapper.JpaI18nEntryMapper;
import de.javaholic.toolkit.i18n.persistence.jpa.provider.JpaTextResolver;
import de.javaholic.toolkit.i18n.persistence.jpa.repo.JpaI18nEntryRepository;
import de.javaholic.toolkit.i18n.persistence.jpa.store.JpaI18nEntryStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
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
    public JpaI18nEntryStore jpaI18nEntryStore(JpaI18nEntryRepository repository, JpaI18nEntryMapper mapper) {
        return new JpaI18nEntryStore(repository, mapper);
    }

    @Bean
    public I18nEntryDtoStore i18nEntryStore(JpaI18nEntryStore domainStore, I18nEntryDtoMapper mapper) {
        return new I18nEntryDtoCrudStore(domainStore, mapper);
    }

    @Bean
    public TextResolver textResolver(JpaI18nEntryStore store) {
        return new JpaTextResolver(store);
    }

}
