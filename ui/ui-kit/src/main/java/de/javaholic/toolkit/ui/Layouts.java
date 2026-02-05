package de.javaholic.util.ui;
// ui.fluent.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Fluent helper for composing simple Vaadin layouts with sensible defaults
 * and stable CSS hooks.
 *
 * <p>
 * Responsibility:
 * <ul>
 *   <li>Layout composition</li>
 *   <li>Readable call-sites</li>
 *   <li>Consistent CSS class naming</li>
 * </ul>
 *
 * <p>
 * Explicitly NOT responsible for:
 * <ul>
 *   <li>Business logic</li>
 *   <li>Validation</li>
 *   <li>I18n</li>
 *   <li>Dialog behavior</li>
 * </ul>
 *
 * <p>
 * CSS hooks applied:
 * <ul>
 *   <li>{@code box}</li>
 *   <li>{@code vertical} / {@code horizontal}</li>
 * </ul>
 *
 * <p>
 * Typical usage:
 * <pre>{@code
 * Component content =
 *     Layouts.vbox(
 *         header,
 *         form,
 *         Layouts.hbox(saveButton, cancelButton)
 *     );
 * }</pre>
 */
public final class Layouts {

    private Layouts() {
    }

    /**
     * Creates a vertical box layout.
     *
     * <p>
     * Applies CSS classes:
     * <ul>
     *   <li>{@code box}</li>
     *   <li>{@code vertical}</li>
     * </ul>
     *
     * <p>
     * Example:
     * <pre>{@code
     * Layouts.vbox(
     *     new H3("User"),
     *     userForm,
     *     actions
     * );
     * }</pre>
     *
     * @param components components to add (order preserved)
     * @return configured {@link VerticalLayout}
     */
    public static VerticalLayout vbox(Component... components) {
        var box = new VerticalLayout();
        box.add(components);
        box.addClassNames("box", "vertical");
        return box;
    }

    /**
     * Creates a horizontal box layout.
     *
     * <p>
     * Applies CSS classes:
     * <ul>
     *   <li>{@code box}</li>
     *   <li>{@code horizontal}</li>
     * </ul>
     *
     * <p>
     * Example:
     * <pre>{@code
     * Layouts.hbox(
     *     saveButton,
     *     cancelButton
     * );
     * }</pre>
     *
     * @param components components to add (order preserved)
     * @return configured {@link HorizontalLayout}
     */
    public static HorizontalLayout hbox(Component... components) {
        var box = new HorizontalLayout();
        box.add(components);
        box.addClassNames("box", "horizontal");
        return box;
    }

    /**
     * Creates a flexible spacer component.
     *
     * <p>
     * Intended to be used inside {@link HorizontalLayout} or {@link VerticalLayout}
     * to push surrounding components apart.
     *
     * <p>
     * Applies CSS class:
     * <ul>
     *   <li>{@code spacer}</li>
     * </ul>
     *
     * <p>
     * Example:
     * <pre>{@code
     * Layouts.hbox(
     *     leftButton,
     *     Layouts.spacer(),
     *     rightButton
     * );
     * }</pre>
     *
     * @return spacer component
     */
    public static Component spacer() {
        var spacer = new Div();
        spacer.addClassName("spacer");
        return spacer;
    }

}
