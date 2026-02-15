package de.javaholic.toolkit.persistence.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DtoCrudStoreTest {

    @Test
    void findAllDelegatesAndMaps() {
        MockCrudStore domainStore = new MockCrudStore();
        domainStore.findAllResult = List.of("domain-a", "domain-b");
        MockDtoMapper mapper = new MockDtoMapper();
        DtoCrudStore<String, String, Long> store = new DtoCrudStore<>(domainStore, mapper);

        List<String> result = store.findAll();

        assertThat(result).containsExactly("dto-domain-a", "dto-domain-b");
        assertThat(mapper.toDtoInputs).containsExactly("domain-a", "domain-b");
        assertThat(domainStore.findAllCalled).isTrue();
    }

    @Test
    void saveDelegatesAndMaps() {
        MockCrudStore domainStore = new MockCrudStore();
        domainStore.saveResult = "domain-saved";
        MockDtoMapper mapper = new MockDtoMapper();
        DtoCrudStore<String, String, Long> store = new DtoCrudStore<>(domainStore, mapper);

        String result = store.save("dto-input");

        assertThat(mapper.toDomainInputs).containsExactly("dto-input");
        assertThat(domainStore.savedInputs).containsExactly("domain-dto-input");
        assertThat(result).isEqualTo("dto-domain-saved");
    }

    @Test
    void deleteMapsAndDelegates() {
        MockCrudStore domainStore = new MockCrudStore();
        MockDtoMapper mapper = new MockDtoMapper();
        DtoCrudStore<String, String, Long> store = new DtoCrudStore<>(domainStore, mapper);

        store.delete("dto-delete");

        assertThat(mapper.toDomainInputs).containsExactly("dto-delete");
        assertThat(domainStore.deletedInputs).containsExactly("domain-dto-delete");
    }

    private static final class MockCrudStore implements CrudStore<String, Long> {
        private List<String> findAllResult = List.of();
        private String saveResult;
        private boolean findAllCalled;
        private final java.util.ArrayList<String> savedInputs = new java.util.ArrayList<>();
        private final java.util.ArrayList<String> deletedInputs = new java.util.ArrayList<>();

        @Override
        public List<String> findAll() {
            findAllCalled = true;
            return findAllResult;
        }

        @Override
        public Optional<String> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public String save(String entity) {
            savedInputs.add(entity);
            return saveResult;
        }

        @Override
        public void delete(String entity) {
            deletedInputs.add(entity);
        }
    }

    private static final class MockDtoMapper implements DtoMapper<String, String> {
        private final java.util.ArrayList<String> toDtoInputs = new java.util.ArrayList<>();
        private final java.util.ArrayList<String> toDomainInputs = new java.util.ArrayList<>();

        @Override
        public String toDto(String domain) {
            toDtoInputs.add(domain);
            return "dto-" + domain;
        }

        @Override
        public String toDomain(String dto) {
            toDomainInputs.add(dto);
            return "domain-" + dto;
        }
    }
}
