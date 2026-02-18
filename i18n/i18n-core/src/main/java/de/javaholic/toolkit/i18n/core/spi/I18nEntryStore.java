package de.javaholic.toolkit.i18n.core.spi;

import de.javaholic.toolkit.i18n.core.domain.I18nEntry;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

/**
 * Domain SPI for i18n entry persistence in the i18n bounded context.
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Define the CRUD boundary for {@link I18nEntry} in core language modules.</li>
 *   <li>Keep i18n core independent from concrete storage technologies.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>i18n persistence adapters (for example JPA adapters).</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>i18n services and optional DTO adapter modules.</li>
 * </ul>
 *
 * <p><strong>Mode Interaction</strong></p>
 * <ul>
 *   <li>Rapid Mode: UI can bind directly to {@code CrudStore<I18nEntry, UUID>}.</li>
 *   <li>Clean Mode: DTO adapter wraps this domain store and exposes {@code CrudStore<I18nEntryDto, UUID>}.</li>
 * </ul>
 */
public interface I18nEntryStore extends CrudStore<I18nEntry, UUID> {
}
