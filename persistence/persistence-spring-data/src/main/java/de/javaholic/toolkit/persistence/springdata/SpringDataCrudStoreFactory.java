package de.javaholic.toolkit.persistence.springdata;

import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.CrudStoreFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.Objects;

public class SpringDataCrudStoreFactory implements CrudStoreFactory {

    private final ApplicationContext context;

    public SpringDataCrudStoreFactory(ApplicationContext context) {
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> CrudStore<T, ?> forType(Class<T> type) {
        Objects.requireNonNull(type, "type");

        JpaRepository repo = resolveJpaRepositoryFor(type);

        // ID-Typ ist hier wildcard; in D machen wir’s sauber typisiert
        return new SpringDataCrudStore(repo);
    }

    @SuppressWarnings("rawtypes")
    private <T> JpaRepository resolveJpaRepositoryFor(Class<T> domainType) {

        Map<String, JpaRepository> repos = context.getBeansOfType(JpaRepository.class);
        if (repos.isEmpty()) {
            throw new IllegalStateException("No JpaRepository beans found in ApplicationContext.");
        }

        JpaRepository match = null;

        for (JpaRepository repo : repos.values()) {
            Class<?> repoDomain = resolveDomainType(repo);
            if (repoDomain == null) {
                continue;
            }
            if (repoDomain.equals(domainType)) {
                if (match != null) {
                    throw new IllegalStateException(
                            "Multiple JpaRepository beans found for domain type " + domainType.getName()
                                    + " (ambiguous). Please provide a unique repository."
                    );
                }
                match = repo;
            }
        }

        if (match == null) {
            throw new IllegalStateException(
                    "No JpaRepository bean found for domain type " + domainType.getName()
            );
        }

        return match;
    }

    private Class<?> resolveDomainType(Object repoBean) {
        // repoBean kann ein Proxy sein, ResolvableType kommt damit i.d.R. klar.
        ResolvableType rt = ResolvableType.forClass(repoBean.getClass()).as(JpaRepository.class);
        if (rt == ResolvableType.NONE) {
            return null;
        }
        return rt.getGeneric(0).resolve();
    }
}
