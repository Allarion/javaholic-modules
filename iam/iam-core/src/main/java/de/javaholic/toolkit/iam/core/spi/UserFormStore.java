package de.javaholic.toolkit.iam.core.spi;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

/**
 * Domain SPI for user persistence operations in the IAM bounded context.
 *
 * <p>This interface is context-specific and extends platform SPI {@link CrudStore}. It follows
 * strict dependency direction: IAM core declares this contract, adapters implement it.</p>
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Define user-specific query and command operations required by IAM core.</li>
 *   <li>Specialize generic CRUD for {@link User} aggregates.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>IAM persistence adapters (for example JPA/file adapters).</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>IAM application/domain services and DTO adapter wiring.</li>
 * </ul>
 *
 * <p><strong>Mode Interaction</strong></p>
 * <ul>
 *   <li>Rapid Mode: may be bound directly as {@code CrudStore<User, UUID>}.</li>
 *   <li>Clean Mode: used as domain delegate behind {@code CrudStore<UserDto, UUID>}.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * UserStore userStore = ...;
 * userStore.findByUsername("admin");
 * userStore.save(user);
 * }</pre>
 */
public interface UserFormStore extends UserQuery, UserCommand, CrudStore<User, UUID> {
}
