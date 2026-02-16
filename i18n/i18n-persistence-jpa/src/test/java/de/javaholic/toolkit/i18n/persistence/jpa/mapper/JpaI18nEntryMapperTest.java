package de.javaholic.toolkit.i18n.persistence.jpa.mapper;

import de.javaholic.toolkit.i18n.persistence.jpa.domain.I18nEntry;
import de.javaholic.toolkit.i18n.persistence.jpa.entity.JpaI18nEntry;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JpaI18nEntryMapperTest {

    private final JpaI18nEntryMapper mapper = new JpaI18nEntryMapper();

    @Test
    void mapsEntityToDomain() {
        UUID id = UUID.randomUUID();
        JpaI18nEntry entity = new JpaI18nEntry(id, "label.ok", "de_DE", "OK", 3L);

        I18nEntry domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getKey()).isEqualTo("label.ok");
        assertThat(domain.getLocale()).isEqualTo("de_DE");
        assertThat(domain.getValue()).isEqualTo("OK");
        assertThat(domain.getVersion()).isEqualTo(3L);
    }

    @Test
    void mapsDomainToEntity() {
        UUID id = UUID.randomUUID();
        I18nEntry domain = new I18nEntry(id, "label.cancel", "en_US", "Cancel", 7L);

        JpaI18nEntry entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getKey()).isEqualTo("label.cancel");
        assertThat(entity.getLocale()).isEqualTo("en_US");
        assertThat(entity.getValue()).isEqualTo("Cancel");
        assertThat(entity.getVersion()).isEqualTo(7L);
    }

    @Test
    void handlesNullValues() {
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toEntity(null)).isNull();
    }
}
