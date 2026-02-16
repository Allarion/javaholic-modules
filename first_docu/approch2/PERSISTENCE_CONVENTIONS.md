# Persistence Conventions

## Stores

CrudStore<T, ID> is UI-facing abstraction.

### JpaDtoCrudStore
- Requires JpaRepository
- Requires EntityMapper

### DtoCrudStore
- Wraps Domain store with DTO mapping

## EntityMapper<D,E>

Explicit conversion.
No reflection-based magic.

## Naming

If store depends on JpaRepository:
→ must contain Jpa in name.

If store wraps Domain:
→ must contain Dto in name.