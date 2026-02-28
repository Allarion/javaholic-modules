
# IAM Security Spring Integration

This module integrates the IAM permission model with Spring Security.

It provides:

- `PermissionChecker` implementation backed by Spring Security
- `CurrentUser` implementation using `SecurityContextHolder`
- `@RequiresPermission` annotation
- `AspectRequiresPermission` for method-level authorization

---

# Architecture Overview

Authorization is separated into two layers:

## 1. Business Authorization (REAL SECURITY)

Annotation:

    @RequiresPermission("iam.user.edit")

Used on:
- Service methods
- Service classes

Enforced by:
- `AspectRequiresPermission`

Behavior:
- Throws `AccessDeniedException` if permission is missing
- Independent of UI or transport layer
- Protects REST and UI equally

This is the primary security mechanism.

---

## 2. UI-Level Permission Control (UX ONLY)

Annotation:

    @UiPermission("iam.user.edit")

Used on:
- DTO fields
- Actions
- UI components

Evaluated by:
- `UiPolicyEngine`

Behavior:
- Hides or disables UI elements
- Does NOT provide security
- Improves user experience only

Never rely solely on UI permissions.

---

# Permission Flow

Spring Security Authentication
↓
AuthenticationAdapter
↓
UserPrincipal
↓
PermissionChecker
↓
@RequiresPermission Aspect

---

# Recommended Usage

## Always protect business logic

Example:

    @RequiresPermission("iam.user.edit")
    public void updateUser(...) {
        ...
    }

UI-level permission handling is optional and only for visibility control.

---

# Routing & Security

- Routes are defined at application level (NOT inside UiSurface).
- Services enforce permissions.
- UI reflects permission state.
- No dependency on Spring `@PreAuthorize` is required.

---

# Permission Naming Convention (Recommended)

Use structured permission keys:

    iam.user.view
    iam.user.edit
    iam.role.edit
    i18n.view
    i18n.edit

Avoid arbitrary strings.

---

# Future Extensions (Optional)

- REST integration (service-level protection remains valid)
- Centralized AccessDenied handling
- Permission hierarchy support
- Role-to-permission mapping extensions

---

# Design Principle

Security is enforced in the service layer.
UI permissions are cosmetic.
Routing is application-specific.

Separation of concerns is intentional.
