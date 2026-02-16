Wie man das in die vorhandene Architektur einbaut
1) i18n-core

Füge HierarchicalI18nResolver in i18n-core ein.

Es wird der zentrale Resolver für UI.

2)Provider Chain konfigurieren

In deinem Spring Boot Bootstrapping:

@Bean
public TextResolver fileProvider() {
return new FileTextResolver(...);
}

@Bean
public TextResolver jpaProvider(JpaI18nEntryStore store) {
return new JpaTextResolver(store);
}

@Bean
public TextResolver builtInProvider() {
return new BuiltInDefaultsProvider();
}

@Bean
public TextResolver textResolver(
List<TextResolver> providers
) {
// Example scope stack for AdminWorkbench
List<String> scopes = List.of(
"Project1.AdminWorkbench",
"AdminWorkbench"
);

    return new HierarchicalI18nResolver(providers, scopes);
}
