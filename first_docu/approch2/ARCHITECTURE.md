# Architecture

## Layers

UI Layer
- Grids
- Forms
- CrudPanels
- TextResolver
- UiMeta

UI Meta Layer
- UiInspector
- UiPropertyInterpreter
- Default + Jpa interpreters

Persistence Layer
- CrudStore<T, ID>
- DtoCrudStore
- JpaDtoCrudStore
- EntityMapper<D, E>

Domain Layer
- Pure business logic

## Key Principles

1. BeanMeta is technical only.
2. UiMeta adds semantic meaning.
3. JPA annotations may be interpreted as metadata markers.
4. Stores are independent of UI.
5. UI never talks directly to JPA repositories.

## Technical Fields

@Id and @Version are treated as technical markers.
Hidden by default in UI.
Override later via explicit UI annotations (future extension).