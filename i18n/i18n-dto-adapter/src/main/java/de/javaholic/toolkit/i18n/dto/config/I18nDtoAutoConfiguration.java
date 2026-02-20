package de.javaholic.toolkit.i18n.dto.config;

import de.javaholic.toolkit.i18n.core.spi.I18nEntryStore;
import de.javaholic.toolkit.i18n.dto.mapper.I18nEntryDtoMapper;
import de.javaholic.toolkit.i18n.dto.spi.I18nEntryDtoStore;
import de.javaholic.toolkit.i18n.dto.store.I18nEntryDtoCrudStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration()
public class I18nDtoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public I18nEntryDtoMapper i18nEntryDtoMapper() {
        return new I18nEntryDtoMapper();
    }

    @Bean
    @ConditionalOnBean(I18nEntryStore.class)
    @ConditionalOnMissingBean(name = "i18nEntryDtoStore")
    public I18nEntryDtoStore i18nEntryDtoStore(I18nEntryStore domainStore, I18nEntryDtoMapper mapper) {
        return new I18nEntryDtoCrudStore(domainStore, mapper);
    }
}
