# Architecture

## Platform Scope

This toolkit evolves towards a modular application platform.

Core capabilities:
- CRUD abstraction
- UI meta interpretation
- I18N
- IAM (Authentication + Authorization)
- User configuration (planned)

Adapters provide:
- Persistence backends
- Security integrations
- External identity providers


## Layer Model

UI Layer - Grids - Forms - CrudPanels - TextResolver

UI Meta Layer - UiInspector - UiMeta - UiProperty -
UiPropertyInterpreter(s)

Persistence Layer - CrudStore\<T, ID\> - DtoCrudStore -
JpaDtoCrudStore - EntityMapper\<D, E\>


CrudStore\<T, ID\> is SPI.
DomainStore implements CrudStore\<Domain\>.
DtoCrudStore implements CrudStore\<DTO\> and wraps DomainStore.
JpaDomainStore implements DomainStore.

Domain Layer - Pure business logic - No UI or JPA dependency

## Interpretation Strategy

1.  BeanIntrospector extracts technical metadata.
2.  UiInspector delegates to UiPropertyInterpreter.
3.  Default interpreter handles UI annotations.
4.  JPA interpreter optionally interprets JPA annotations.

## Technical Fields

@Id and @Version are treated as technical markers. Hidden by default in
UI.

## Clean vs Rapid Mode

Clean Mode: DTO → Store → Domain → JPA

Rapid Mode: JPA Entity used directly in UI.

Both are supported intentionally.

## DTO Strategy

DTO is optional and exists as an adapter layer.
Core must never depend on DTO.
Rapid Mode is implemented by binding CrudStore<Entity> instead of CrudStore<DTO>.
Clean Mode uses DTO adapter.



## Adapter Model

Core defines SPI.
Adapters implement SPI.
UI depends only on SPI.
Persistence depends only on Domain.

Core Layer:
- Domain
- SPI interfaces
- No DTO
- No JPA
- No Spring

Adapter Layer:
- JPA
- File
- DTO
- Spring Security

## Official SPI List
- CrudStore<T>
- CurrentUser
- AuthenticationService
- PermissionChecker
- TextResolver

## Dependency Direction

Allowed dependency flow (bottom → top):

core → persistence → dto-adapter → ui → spring-starter

Rules:
- core must not depend on any other module
- persistence may depend only on core
- dto-adapter may depend only on core
- ui may depend on core and CrudStore SPI
- spring-starter may depend on all adapters

## Bean Ownership Rules

- Only adapter modules may provide concrete SPI implementations.
- Persistence modules must not expose high-level SPI directly.
- DTO adapters wrap DomainStore and publish SPI.
- Core must never define Spring Beans.
- AutoConfiguration classes live only in adapter or starter modules.

## I18N
Scope prefixes allow app-level overrides.

Example:
ProjectA.AdminWorkbench
AdminWorkbench

These prefixes are optionally passed to the resolver,
so that keys in these scopes are tested before core keys.

e.g.:
ProjectA.AdminWorkbench.crud.user.title
AdminWorkbench.crud.user.title
crud.user.title