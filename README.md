# javaholic-modules

This repository contains a set of modular, reusable Java building blocks
used across multiple projects.

The guiding principles are:

- strong domain separation
- headless core modules
- infrastructure via adapters
- minimal framework coupling
- long-term maintainability

---

## Module Overview

### IAM (Identity & Access Management)

iam/
├─ iam-core
├─ iam-persistence-file
├─ iam-persistence-jpa
├─ iam-security-spring
└─ iam-ui (planned)


#### iam-core
The authoritative IAM domain.

- Users, Roles, Permissions
- Authorization API
- SPI for persistence and authentication
- No UI
- No Spring Security dependency
- No persistence technology dependency

This module represents **truth over time**.

#### iam-persistence-file
File-based persistence adapter.

- YAML / JSON backed
- Intended for bootstrap, tests, demos
- Implements iam-core SPI

#### iam-persistence-jpa
JPA-based persistence adapter.

- Spring Data JPA
- Flyway for schema versioning (minimal)
- Domain remains JPA-free
- Database selection is runtime concern

#### iam-security-spring
Spring Security integration.

- Maps Authentication to UserPrincipal
- Provides PermissionChecker
- Optional method-security enforcement
- No domain logic

#### iam-ui (planned)
Administrative UI.

- User management
- Role assignment
- Permission inspection
- Will drive real i18n usage

---

## Roadmap (High Level)

Phase 1: IAM Core (done)
Phase 2: Persistence (file + JPA)
Phase 3: IAM Management API
Phase 4: IAM Base UI
Phase 5: i18n Core + Editor
Phase 6: Config Core + Forms
Phase 7: AdminWorkbench


---

## Architectural Principles

- Domain entities represent persisted truth
- Principals are runtime projections
- Infrastructure is replaceable
- Enforcement is separate from policy
- UI is never built without a real domain

---

## Status

This repository is under active development.
APIs may evolve until the first integrated AdminWorkbench is complete.

---
##  UI Semantic Annotations & i18n Integration

The toolkit now supports declarative UI annotations to influence
visibility, label keys, ordering, and readOnly semantics.

Annotations:

- `@UiHidden`
- `@UiLabel(key="...")`
- `@UiOrder(value=...)`
- `@UiReadOnly`

`UiMeta` is responsible for evaluation.
Builders use `labelKey` resolved via `TextResolver`.

Example:

```java
@UiHidden
private UUID id;

@UiLabel(key = "user.email.label")
@UiOrder(10)
private String email;
```

i18n integration:
Provide a `TextResolver` to `Grids.auto()` / `Forms.auto()` to
resolve these keys to display text.
