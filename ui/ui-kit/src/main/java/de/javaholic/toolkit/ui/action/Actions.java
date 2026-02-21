package de.javaholic.toolkit.ui.action;

/**
 * Action factory entry points.
 *
 * <pre>{@code
 * Action save = Actions.create().label("save").onClick(this::save).build();
 * Action cancel = Actions.noop("cancel");
 * }</pre>
 */
public final class Actions {

    private Actions() {
    }

    /**
     * Creates a fluent builder for {@link Action}.
     */
    public static ActionBuilder create() {
        return new ActionBuilder();
    }

    /**
     * Creates a no-op action.
     *
     */
    // FIXME: WTF: Action.noop ?? lol
    public static Action noop(String label) {
        return create().label(label).onClick(() -> { }).build();
    }
}
