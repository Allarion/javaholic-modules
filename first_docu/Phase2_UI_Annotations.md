# Phase 2 - UI Semantic Annotations

## Overview

Phase 2 adds declarative UI semantics on model properties without changing the technical introspection layer.

Architecture flow:

`BeanMeta (technical) -> UiMeta (semantic) -> Grid/Form builders -> FieldRegistry -> UI components`

Rules:

- `BeanMeta` stays technical only.
- UI annotation evaluation happens in `UiInspector`/`UiMeta`.
- Builders consume semantic metadata, not raw annotation reflection.
- Label text resolution is externalized through `TextResolver`.

## Supported Annotations

- `@UiHidden`
- `@UiLabel(key = "...")`
- `@UiOrder(value = ...)`
- `@UiReadOnly`

Targets: field and getter method.

## Sample Model

```java
public class UserDto {
    @UiHidden
    private UUID id;

    @UiLabel(key = "user.email.label")
    @UiOrder(10)
    private String email;

    @UiReadOnly
    @UiOrder(20)
    private String externalId;
}
```

## Auto Grid/Form Usage

```java
Grid<UserDto> grid = Grids.auto(UserDto.class)
        .withTextResolver(key -> messages.getOrDefault(key, key))
        .build();

Forms.Form<UserDto> form = Forms.auto(UserDto.class)
        .withTextResolver(key -> messages.getOrDefault(key, key))
        .build();
```

## TextResolver Integration

Use `TextResolver` to resolve `UiProperty.labelKey()` to display text during rendering:

```java
TextResolver resolver = key -> switch (key) {
    case "user.email.label" -> "E-Mail";
    default -> key;
};
```

This keeps i18n resolution outside `UiMeta` and inside UI construction.

## Minimal App Snippet

```java
public final class UsersView extends VerticalLayout {
    public UsersView() {
        TextResolver resolver = key -> "i18n:" + key;

        Grid<UserDto> grid = Grids.auto(UserDto.class)
                .withTextResolver(resolver)
                .build();

        Forms.Form<UserDto> form = Forms.auto(UserDto.class)
                .withTextResolver(resolver)
                .build();

        add(grid, form.layout());
    }
}
```
