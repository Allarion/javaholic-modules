package de.javaholic.toolkit.i18n.ui;

import com.vaadin.flow.component.notification.Notification;
import de.javaholic.toolkit.ui.api.ResourceAction;

public final class I18nUiActions {

    private I18nUiActions() {
    }

    public static <T> ResourceAction.ToolbarAction<T> importAction() {
        return ResourceAction.toolbar("Import...", () ->
                Notification.show("Import is not implemented yet.", 2500, Notification.Position.MIDDLE)
        );
    }

    public static <T> ResourceAction.ToolbarAction<T> exportAction() {
        return ResourceAction.toolbar("Export...", () ->
                Notification.show("Export is not implemented yet.", 2500, Notification.Position.MIDDLE)
        );
    }

    public static <T> ResourceAction.ToolbarAction<T> generateMissingKeysAction() {
        return ResourceAction.toolbar("Generate Missing Keys", () ->
                Notification.show("Generate Missing Keys is not implemented yet.", 2500, Notification.Position.MIDDLE)
        );
    }
}
