package de.javaholic.toolkit.i18n.dto.config;

import de.javaholic.toolkit.i18n.core.spi.I18nEntryStore;
import de.javaholic.toolkit.i18n.dto.dto.I18nEntryDto;
import de.javaholic.toolkit.i18n.dto.mapper.I18nEntryDtoMapper;
import de.javaholic.toolkit.i18n.dto.store.I18nEntryDtoCrudStore;
import de.javaholic.toolkit.persistence.core.CrudStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@AutoConfiguration
@ConditionalOnBean(I18nEntryStore.class)
public class I18nDtoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public I18nEntryDtoMapper i18nEntryDtoMapper() {
        return new I18nEntryDtoMapper();
    }

    @Bean
    @ConditionalOnMissingBean(name = "i18nCrudStore")
    public CrudStore<I18nEntryDto, UUID> i18nCrudStore(I18nEntryStore domainStore, I18nEntryDtoMapper mapper) {
        return new I18nEntryDtoCrudStore(domainStore, mapper);
    }
}
