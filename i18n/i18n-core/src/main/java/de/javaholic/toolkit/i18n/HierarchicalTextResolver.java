package de.javaholic.toolkit.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * {@link TextResolver} that performs hierarchical key resolution
 * with optional scope prefixes and a provider chain.
 *
 * <p>
 * Resolution proceeds in three conceptual stages:
 * <ol>
 *     <li><strong>Scope Prefixes</strong> – optional prefixes to allow
 *     application-level overrides (e.g. "ProjectA.AdminWorkbench").</li>
 *     <li><strong>Key Hierarchy</strong> – fallback candidates derived
 *     from a canonical key (e.g. for
 *     "crud.user.dialog.select.confirm.label", candidates include
 *     "crud.user.dialog.select.confirm.label",
 *     "crud.dialog.select.confirm.label", etc.).</li>
 *     <li><strong>Provider Chain</strong> – each candidate key is
 *     resolved against a stack of {@link TextResolver} in order.</li>
 * </ol>
 *
 * <p>
 * If no match is found, the original key is returned.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * List<TextResolver> providers = List.of(
 *     new RuntimeOverrideProvider(),
 *     jpaProvider,
 *     fileProvider,
 *     new BuiltInDefaultsProvider()
 * );
 * List<String> scopePrefixes = List.of(
 *     "ProjectA.AdminWorkbench",
 *     "AdminWorkbench"
 * );
 * TextResolver resolver =
 *     new HierarchicalI18nResolver(providers, scopePrefixes);
 *
 * String message = resolver.resolve("crud.user.select.title");
 * }</pre>
 */
public class HierarchicalTextResolver implements TextResolver {

    private final List<TextResolver> providers;
    private final List<String> scopePrefixes;

    public HierarchicalTextResolver(
            List<TextResolver> providers,
            List<String> scopePrefixes
    ) {
        this.providers = providers == null
                ? Collections.emptyList()
                : List.copyOf(providers);
        this.scopePrefixes = scopePrefixes == null
                ? Collections.emptyList()
                : List.copyOf(scopePrefixes);
    }

    @Override
    public Optional<String> resolve(String key, Locale locale) {
        List<String> scopedKeys = buildScopedKeys(key);

        for (String candidate : scopedKeys) {
            for (TextResolver provider : providers) {
                Optional<String> v = provider.resolve(candidate, locale);
                if (v.isPresent() && !v.get().equals(key)) {
                    return v;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Builds the ordered list of keys to try, combining:
     * - scope prefixes
     * - hierarchical fallback
     *
     * @param key canonical key
     * @return list of candidate keys in fallback order
     */
    private List<String> buildScopedKeys(String key) {
        List<String> result = new ArrayList<>();

        // first: per-scope key variants
        for (String scope : scopePrefixes) {
            String scopedBase = scope + "." + key;
            result.addAll(expandHierarchy(scopedBase));
        }

        // then: app-level key hierarchy
        result.addAll(expandHierarchy(key));

        return result;
    }

    /**
     * Expands a canonical key into fallback candidates in
     * descending specificity.
     *
     * Example:
     *   "crud.user.dialog.select.confirm.label" yields:
     *   [
     *     "crud.user.dialog.select.confirm.label",
     *     "crud.dialog.select.confirm.label",
     *     "dialog.select.confirm.label",
     *     "select.confirm.label"
     *   ]
     *
     * @param key canonical key
     * @return hierarchical fallback keys
     */
    private List<String> expandHierarchy(String key) {
        String[] parts = key.split("\\.");
        List<String> expanded = new ArrayList<>();

        for (int i = 0; i < parts.length; i++) {
            String candidate = String.join(
                    ".",
                    java.util.Arrays.copyOfRange(parts, i, parts.length)
            );
            expanded.add(candidate);
        }
        return expanded;
    }
}
