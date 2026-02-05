/**
 * Audit Core.
 *
 * <p>
 * Provides infrastructure for recording domain-level audit events
 * such as security-relevant or business-relevant actions.
 * </p>
 *
 * <p>
 * This is <strong>not</strong> technical logging (SLF4J, Logback).
 * Audit events are structured, queryable records.
 * </p>
 *
 * <p>
 * Design principles:
 * <ul>
 *   <li>UI-agnostic</li>
 *   <li>Explicit domain events</li>
 *   <li>Callable from any core module</li>
 * </ul>
 * </p>
 * <p>
 * Intended usage:
 * <ul>
 *   <li>Security-relevant actions (role changes, config edits)</li>
 *   <li>Business events (workflow started/finished)</li>
 * </ul>
 * </p>
 *
 * <p>
 * This module is UI-agnostic and may be used from any core module.
 * </p>
 */
package de.javaholic.toolkit.audit.core;

