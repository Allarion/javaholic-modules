package de.javaholic.toolkit.i18n.ui;

import com.vaadin.flow.component.notification.Notification;
import de.javaholic.toolkit.ui.crud.action.CrudAction;
import de.javaholic.toolkit.ui.crud.action.CrudPreset;

/**
 * I18n-specific CRUD defaults and helper actions.
 *
 * <p>Presets configure default create/edit/delete actions only. Callers can still add
 * additional custom actions through the CRUD builder API.</p>
 */
public final class I18nUiPresets {

    private static final CrudPreset TEXTS = new FixedPreset(true, true, true);

    private I18nUiPresets() {
    }

    public static CrudPreset texts() {
        return TEXTS;
    }

    public static <T> CrudAction.ToolbarAction<T> importAction() {
        return CrudAction.toolbar("Import...", () ->
                Notification.show("Import is not implemented yet.", 2500, Notification.Position.MIDDLE)
        );
    }

    public static <T> CrudAction.ToolbarAction<T> exportAction() {
        return CrudAction.toolbar("Export...", () ->
                Notification.show("Export is not implemented yet.", 2500, Notification.Position.MIDDLE)
        );
    }

    public static <T> CrudAction.ToolbarAction<T> generateMissingKeysAction() {
        return CrudAction.toolbar("Generate Missing Keys", () ->
                Notification.show("Generate Missing Keys is not implemented yet.", 2500, Notification.Position.MIDDLE)
        );
    }

    private record FixedPreset(boolean enableCreate, boolean enableEdit, boolean enableDelete) implements CrudPreset {
    }
}
