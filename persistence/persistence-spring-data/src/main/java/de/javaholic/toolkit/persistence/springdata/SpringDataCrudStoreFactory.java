package de.javaholic.toolkit.persistence.springdata;

import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.CrudStoreFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SpringDataCrudStoreFactory implements CrudStoreFactory {

    private final ApplicationContext context;

    private final ConcurrentHashMap<Class<?>, JpaRepository<?, ?>> repoCache = new ConcurrentHashMap<>();

    private <T> JpaRepository<?, ?> repoFor(Class<T> type) {
        return repoCache.computeIfAbsent(type, this::resolveJpaRepositoryFor);
    }

    public SpringDataCrudStoreFactory(ApplicationContext context) {
        this.context = Objects.requireNonNull(context, "context");
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> CrudStore<T, ?> forType(Class<T> type) {
        Objects.requireNonNull(type, "type");

        JpaRepository repo = repoFor(type);
        Class<?> idType = resolveIdType(repo);

        return new SpringDataCrudStore(type, idType, repo);
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
        ResolvableType rt = ResolvableType.forClass(repoBean.getClass()).as(JpaRepository.class);
        if (rt == ResolvableType.NONE) {
            return null;
        }
        return rt.getGeneric(0).resolve();
    }

    private Class<?> resolveIdType(Object repoBean) {
        ResolvableType rt = ResolvableType.forClass(repoBean.getClass()).as(JpaRepository.class);
        if (rt == ResolvableType.NONE) {
            throw new IllegalStateException("Cannot resolve JpaRepository ID type.");
        }
        Class<?> idType = rt.getGeneric(1).resolve();
        if (idType == null) {
            throw new IllegalStateException("Cannot resolve JpaRepository ID type.");
        }
        return idType;
    }
}
