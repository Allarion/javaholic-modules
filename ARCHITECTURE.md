# Architecture

## 1. Platform Scope

This project is a modular application platform, not only a CRUD toolkit.

Core capabilities:

-   CRUD abstraction
-   UI meta interpretation
-   I18N
-   IAM (Authentication + Authorization)
-   User configuration (planned)

The platform is designed around a stable SPI core with pluggable
adapters.

------------------------------------------------------------------------

## 2. Architectural Principles

1.  No hidden global state\
2.  No reflection-based mapping magic\
3.  Explicit mapping between layers\
4.  Strict dependency direction\
5.  SPI-first design\
6.  Adapters are replaceable\
7.  Rapid Mode is not a shortcut architecture

------------------------------------------------------------------------

## 3. Layer Model (Conceptual)

### Domain Layer

-   Pure business logic
-   No UI dependency
-   No JPA dependency
-   No Spring dependency
-   Defines SPI interfaces

### Persistence Adapter Layer

-   Implements Domain SPI
-   Contains:
   -   JPA entities
   -   Repositories
   -   Entity ↔ Domain mapping
-   Must not expose high-level SPI directly

### DTO Adapter Layer (optional)

-   Wraps DomainStore
-   Provides DTO ↔ Domain mapping
-   Publishes CrudStore`<DTO>`{=html}
-   Must not depend on JPA entities

### UI Layer

-   ResourcePanels
-   Forms
-   Grids
-   Login / Account (future)
-   Depends only on SPI (CrudStore, Auth SPI, etc.)
-   Must not depend on JPA

### Spring Starter Layer

-   AutoConfiguration only
-   Wires adapters together
-   May depend on all adapter modules

------------------------------------------------------------------------

## 4. Crud Abstraction Model

CrudStore\<T, ID\> is the central SPI.

Definitions:

-   DomainStore implements CrudStore`<Domain>`{=html}
-   JpaDomainStore implements DomainStore
-   DtoCrudStore implements CrudStore`<DTO>`{=html} and wraps
    DomainStore
-   Rapid Mode binds CrudStore`<Entity>`{=html}

CrudStore is not a persistence construct. It is a platform-level
abstraction.

------------------------------------------------------------------------

## 5. Clean vs Rapid Mode

Clean Mode: DTO → CrudStore`<DTO>`{=html} → DomainStore → JPA

Rapid Mode: CrudStore`<Entity>`{=html} → JPA

Important:

Rapid Mode is implemented by binding CrudStore`<Entity>`{=html}. It does
not introduce architectural shortcuts. UI still depends only on
CrudStore.

------------------------------------------------------------------------

## 6. DTO Strategy

-   DTO is an adapter layer
-   Core must never depend on DTO
-   DTO must not depend on JPA entities
-   Rapid Mode bypasses DTO adapter
-   Clean Mode enables DTO adapter

DTO existence must not change dependency direction.

------------------------------------------------------------------------

## 7. Adapter Model

Core defines SPI. Adapters implement SPI. UI depends only on SPI.
Persistence depends only on Domain.

Adapters include:

-   JPA
-   File
-   DTO
-   Spring Security
-   External Identity Providers (future)

------------------------------------------------------------------------

## 8. Official SPI List

Platform SPI:

-   CrudStore\<T, ID\>
-   CurrentUser
-   AuthenticationService
-   PermissionChecker
-   TextResolver

Only SPI interfaces may be consumed by UI or other adapters.

------------------------------------------------------------------------

## 9. Dependency Direction (Strict)

Allowed flow:

core\
→ persistence\
→ dto-adapter\
→ ui\
→ spring-starter

Rules:

-   core must not depend on any other module
-   persistence may depend only on core
-   dto-adapter may depend only on core
-   ui may depend on core and SPI only
-   spring-starter may depend on all adapters
-   no upward dependency allowed

------------------------------------------------------------------------

## 10. Bean Ownership Rules

-   Core must never define Spring Beans
-   Persistence must publish only DomainStore
-   DTO adapters publish CrudStore`<DTO>`{=html}
-   UI must not publish persistence beans
-   AutoConfiguration classes exist only in adapter or starter modules
-   Bean overriding is forbidden by design

------------------------------------------------------------------------

## 11. Interpretation Strategy

1.  BeanIntrospector extracts technical metadata.
2.  UiInspector delegates to UiPropertyInterpreter.
3.  Default interpreter handles UI annotations.
4.  JPA interpreter optionally interprets JPA annotations.

Interpretation must remain pluggable and deterministic.

------------------------------------------------------------------------

## 12. Technical Fields

@Id and @Version are treated as technical markers. Hidden by default in
UI. Interpreted, not required.

------------------------------------------------------------------------

## 13. I18N Model

Scope prefixes allow app-level overrides.

Resolution order example:

ProjectA.AdminWorkbench.crud.user.title\
AdminWorkbench.crud.user.title\
crud.user.title

TextResolver returns key if not found.

I18N is SPI-based and adapter-driven.

