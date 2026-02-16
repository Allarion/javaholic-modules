Wie man das in die vorhandene Architektur einbaut
1) i18n-core

Füge HierarchicalI18nResolver in i18n-core ein.

Es wird der zentrale Resolver für UI.

2)Provider Chain konfigurieren

In deinem Spring Boot Bootstrapping:

@Bean
public I18nProvider fileProvider() {
return new FileI18nProvider(...);
}

@Bean
public I18nProvider jpaProvider(JpaI18nEntryStore store) {
return new JpaI18nProvider(store);
}

@Bean
public I18nProvider builtInProvider() {
return new BuiltInDefaultsProvider();
}

@Bean
public TextResolver textResolver(
List<I18nProvider> providers
) {
// Example scope stack for AdminWorkbench
List<String> scopes = List.of(
"Project1.AdminWorkbench",
"AdminWorkbench"
);

    return new HierarchicalI18nResolver(providers, scopes);
}
