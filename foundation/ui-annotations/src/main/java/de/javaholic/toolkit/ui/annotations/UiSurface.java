package de.javaholic.toolkit.ui.annotations;

import java.lang.annotation.*;

/**
 * Declarative surface metadata for a DTO type.
 *
 * <p>This annotation is declarative only and must not contain runtime UI logic.</p>
 * <p>If {@link #actions()} is {@link Void}, no action provider is configured.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UiSurface {

    Class<?> view(); // default ResourcePanel.class;

    Class<?> actions() default Void.class;
}

// TOFO: geplant war mehrere pro dto, dann mglw mit mehreren actions...aber das führt dann zu permission x in a, y in b, feld xyz ist sichtbar bei a aber hidden bei b...
// lieber dann doch noch ne IDENTISCHE DTO anlegen mit anderer @UiSurface deklaration (identische Dto sogar mit interface markieren (SaneButDifferent or something welche der mapper interpretieren kann))