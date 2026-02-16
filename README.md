# Javaholic Modules

A modular toolkit ecosystem for building structured Java applications
with:

-   Clean UI meta interpretation
-   Fluent UI builders
-   DTO-first persistence model
-   Explicit mapping (no reflection magic)
-   Convention over configuration

## Core Ideas

1.  Technical introspection is separated from UI semantics.
2.  UI works on `<T>` (preferably DTO).
3.  Persistence is abstracted via `CrudStore<T, ID>`.
4.  JPA is optional and interpreted as metadata, not required.
5.  Naming and key conventions are part of the architecture.

## Modules (Conceptual)

-   foundation (introspection)
-   ui-kit
-   ui-kit-crud
-   persistence-core
-   persistence-springdata (optional integration)
-   iam (reference implementation)

## Philosophy

Minimal magic. Explicit mapping. Composable layers. Extensible but
predictable.
