package de.javaholic.toolkit.ui.api;

import java.util.Optional;

public interface UiSurfaceContext<T> {

    Class<T> dtoType();

    Optional<T> currentSelection();

    void refresh();

    // TODO: (later version) add fields: PermissionChecker, CurrentUser, Store
}

