# Javaholic Toolkit – Overview

Lightweight UI + Persistence Toolkit built around:
- Fluent UI builders (Grid, Form, CRUD)
- UI Meta interpretation layer
- Store abstraction (DTO-first)
- Optional JPA integration
- Convention over configuration

## Philosophy

- UI works on `<T>` (DTO preferred).
- Persistence is separate.
- JPA is optional.
- DTO-first is clean mode.
- JPA-only is rapid mode.
- No magic hidden coupling.

## Modes

### Clean Mode (recommended)
DTO → Store → Domain → JPA

### Rapid Mode
JPA Entity directly used as UI model.

## Not Implemented (Intentionally)
- Context-based visibility
- Permission enforcement
- Global resolver wiring
- Full role-based field filtering

These are extension points.