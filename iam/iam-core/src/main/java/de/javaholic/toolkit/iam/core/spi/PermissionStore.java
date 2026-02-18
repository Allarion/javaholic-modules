package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

/**
 * Domain SPI for permission persistence operations in the IAM bounded context.
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Provide permission-specific query/command operations.</li>
 *   <li>Specialize {@link CrudStore} for {@link Permission} entities.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>IAM persistence adapters (for example JPA adapters).</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>IAM authorization services and DTO adapter wiring.</li>
 * </ul>
 *
 * <p><strong>Mode Interaction</strong></p>
 * <ul>
 *   <li>Rapid Mode: direct binding as {@code CrudStore<Permission, UUID>}.</li>
 *   <li>Clean Mode: delegated by DTO-facing permission CRUD stores.</li>
 * </ul>
 */
public interface PermissionStore extends PermissionCommand, PermissionQuery, CrudStore<Permission, UUID> {
}
