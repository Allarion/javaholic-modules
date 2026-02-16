package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;

/**
 * Interprets one technical bean property into UI semantic metadata.
 *
 * <p>Implementations encapsulate annotation and convention rules used by {@link UiInspector}.</p>
 */
public interface UiPropertyInterpreter {

    /**
     * Creates one {@link UiProperty} from the technical property metadata.
     *
     * <p>Example: {@code UiProperty<User> p = interpreter.interpret(User.class, prop, beanMeta);}</p>
     */
    <T> UiProperty<T> interpret(
            Class<T> beanType,
            BeanProperty<T, ?> property,
            BeanMeta<T> beanMeta
    );
}
