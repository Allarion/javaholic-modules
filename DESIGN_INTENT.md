# Design Intent

This project exists to reduce repetitive UI + persistence boilerplate
while keeping architecture explicit and understandable.

## Naming Conventions

Key structure examples:

-   form.user.email.label
-   form.user.email.placeholder
-   dialog.user.edit.title
-   grid.user.delete.label

Keys are hierarchical: component.entity.property.aspect

TextResolver returns the key if no translation is found.

## Architectural Goals

-   No hidden global state
-   No reflection-based mapping magic
-   UI never directly depends on JPA
-   Mapping is explicit and injectable
-   Interpretation is pluggable

## What Is Intentionally Deferred

-   Context-based visibility rules
-   Permission enforcement in UI
-   Global resolver injection
-   Automatic role filtering

The system is designed for extension, not premature complexity.
