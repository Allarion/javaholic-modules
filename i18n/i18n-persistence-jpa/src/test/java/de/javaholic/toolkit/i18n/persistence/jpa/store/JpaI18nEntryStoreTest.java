package de.javaholic.toolkit.i18n.persistence.jpa.store;

import de.javaholic.toolkit.i18n.core.domain.I18nEntry;
import de.javaholic.toolkit.i18n.persistence.jpa.entity.JpaI18nEntry;
import de.javaholic.toolkit.i18n.persistence.jpa.mapper.JpaI18nEntryMapper;
import de.javaholic.toolkit.i18n.persistence.jpa.repo.JpaI18nEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaI18nEntryStoreTest {

    @Mock
    private JpaI18nEntryRepository repository;

    @Mock
    private JpaI18nEntryMapper mapper;

    @InjectMocks
    private JpaI18nEntryStore store;

    @Test
    void findAllDelegatesAndMaps() {
        JpaI18nEntry entity = new JpaI18nEntry();
        I18nEntry domain = new I18nEntry();
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<I18nEntry> result = store.findAll();

        assertThat(result).containsExactly(domain);
        verify(repository).findAll();
        verify(mapper).toDomain(entity);
    }

    @Test
    void findByIdDelegatesAndMaps() {
        UUID id = UUID.randomUUID();
        JpaI18nEntry entity = new JpaI18nEntry();
        I18nEntry domain = new I18nEntry();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<I18nEntry> result = store.findById(id);

        assertThat(result).contains(domain);
        verify(repository).findById(id);
        verify(mapper).toDomain(entity);
    }

    @Test
    void saveDelegatesAndMaps() {
        I18nEntry input = new I18nEntry();
        JpaI18nEntry mappedEntity = new JpaI18nEntry();
        JpaI18nEntry persistedEntity = new JpaI18nEntry();
        I18nEntry persistedDomain = new I18nEntry();
        when(mapper.toEntity(input)).thenReturn(mappedEntity);
        when(repository.save(mappedEntity)).thenReturn(persistedEntity);
        when(mapper.toDomain(persistedEntity)).thenReturn(persistedDomain);

        I18nEntry result = store.save(input);

        assertThat(result).isSameAs(persistedDomain);
        verify(mapper).toEntity(input);
        verify(repository).save(mappedEntity);
        verify(mapper).toDomain(persistedEntity);
    }

    @Test
    void deleteDelegatesAndMaps() {
        I18nEntry input = new I18nEntry();
        JpaI18nEntry mappedEntity = new JpaI18nEntry();
        when(mapper.toEntity(input)).thenReturn(mappedEntity);

        store.delete(input);

        verify(mapper).toEntity(input);
        verify(repository).delete(mappedEntity);
    }
}

