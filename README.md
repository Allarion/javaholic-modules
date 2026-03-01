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
-   ui-kit-resource
-   persistence-core
-   persistence-springdata (optional integration)
-   iam (reference implementation)

## Philosophy

Minimal magic. Explicit mapping. Composable layers. Extensible but
predictable.


--
## Endgame Vision: 
App Generator / Toolkit:
Define DTOs (name + fields) with @UiSurface define the view (from provided views (extensible))
Define actions (name at this point, rest is impl.)

Generate Code for CrudBeans (maybe better to go CQRS) + Services + Actions stubs
Code actions stub.

--
Also JPA Editor (mit mutate function to generate flyway oä) aus +/- feld/relation
Domain (optional) daraus ableitbar (wieder mapper definition)

Dann könnte der DTO editor die bekannten domain objekte (alternativ JPA) und deren felder anbieten. daraus kann man fix neue dtos bauen.