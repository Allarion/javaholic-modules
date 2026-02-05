/**
 * Configuration Core Module.
 *
 * <p>
 * Defines the runtime configuration model used across projects.
 * Configuration objects are plain Java beans discovered by convention
 * (e.g. {@code @ConfigurationProperties}).
 * </p>
 *
 * <p>
 * This module intentionally avoids UI concerns.
 * UI layers interpret configuration metadata separately.
 * </p>
 *
 * <p>
 * Design principles:
 * <ul>
 *   <li>Convention over interfaces</li>
 *   <li>Bean Validation for correctness</li>
 *   <li>Optional annotations for UI metadata</li>
 * </ul>
 * </p>
 */

/**
 * Configuration Core.
 *
 * <p>
 * Defines the runtime configuration model shared across projects.
 * Configuration objects are plain Java beans discovered by convention.
 * </p>
 *
 * <p>
 * No UI logic is contained in this module.
 * UI layers interpret configuration metadata separately.
 * </p>
 *
 * <p>
 * Design principles:
 * <ul>
 *   <li>Convention over interfaces</li>
 *   <li>Plain Java beans as config objects</li>
 *   <li>Bean Validation for correctness</li>
 *   <li>Optional annotations for UI metadata</li>
 * </ul>
 * </p>
 */
package de.javaholic.toolkit.config.core;
