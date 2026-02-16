# Architecture

## Layer Model

UI Layer - Grids - Forms - CrudPanels - TextResolver

UI Meta Layer - UiInspector - UiMeta - UiProperty -
UiPropertyInterpreter(s)

Persistence Layer - CrudStore\<T, ID\> - DtoCrudStore -
JpaDtoCrudStore - EntityMapper\<D, E\>

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