# IAM Modules

This folder contains the headless IAM foundation. Core domain types live in
`iam/iam-core`, while persistence and security are attached via SPI adapters.

## Persistence

IAM core defines ports in `de.javaholic.toolkit.iam.core.spi`. Implementations
are free to load users/roles from any source. To switch persistence, bind a
different `UserStore`/`RoleStore` implementation at construction time.

Example (file-based):

```java
UserStore store = new FileUserStore();
```

## Security Integration

Spring Security integration is optional and lives in `iam/iam-security-spring`.
Provide one or more `AuthenticationAdapter` implementations and use
`SpringCurrentUser` plus `DefaultPermissionChecker`.

Example:

```java
CurrentUser currentUser = new SpringCurrentUser(List.of(adapter));
PermissionChecker checker = new DefaultPermissionChecker(currentUser);
```
