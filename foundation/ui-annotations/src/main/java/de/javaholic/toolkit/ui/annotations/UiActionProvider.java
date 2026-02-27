package de.javaholic.toolkit.ui.annotations;

import java.util.List;

@FunctionalInterface
public interface UiActionProvider<T> {
    List<ResourceAction<T>> actions(UiSurfaceContext<T> context);
}
