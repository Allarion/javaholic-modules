# UI Conventions

## UiMeta

- Created via UiInspector.
- Uses interpreters.
- No rendering logic.
- No store logic.

## Ui Annotations

- @UiLabel
- @UiHidden
- @UiPermission
- @UIRequired
- @UiOrder
- @UiReadOnly

JPA annotations may influence interpretation only in Jpa interpreter.

## TextResolver

- Always expects key.
- Default returns key if not found.
- No implicit global resolver.