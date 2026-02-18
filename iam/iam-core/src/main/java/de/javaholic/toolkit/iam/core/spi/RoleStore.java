package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

/**
 * Domain SPI for role persistence operations in the IAM bounded context.
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Define role-specific query and command operations for IAM policies.</li>
 *   <li>Specialize {@link CrudStore} for {@link Role}.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>IAM persistence adapters.</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>IAM services, authorization flows, and DTO adapter layer.</li>
 * </ul>
 *
 * <p><strong>Mode Interaction</strong></p>
 * <ul>
 *   <li>Rapid Mode: can be consumed directly as {@code CrudStore<Role, UUID>}.</li>
 *   <li>Clean Mode: serves as domain backing store for DTO CRUD adapters.</li>
 * </ul>
 */
public interface RoleStore extends RoleQuery, RoleCommand, CrudStore<Role, UUID> {
}
