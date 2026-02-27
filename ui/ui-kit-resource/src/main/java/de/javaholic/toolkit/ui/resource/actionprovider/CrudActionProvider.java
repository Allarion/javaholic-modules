package de.javaholic.toolkit.ui.resource.actionprovider;

import de.javaholic.toolkit.ui.api.ResourceAction;
import de.javaholic.toolkit.ui.api.UiActionProvider;
import de.javaholic.toolkit.ui.api.UiSurfaceContext;
import de.javaholic.toolkit.ui.resource.action.ResourceActions;

import java.util.List;

public class CrudActionProvider<T> implements UiActionProvider<T> {

    @Override
    public List<ResourceAction<T>> actions(UiSurfaceContext<T> context) {

        return List.of(
                ResourceActions.<T>toolbar("Create")
                        .onInvoke(context.view()::create)
                        .build(),

                ResourceActions.<T>item("Edit")
                        .onInvoke(item -> context.view().edit(item))
                        .build(),

                ResourceActions.<T>item("Delete")
                        .onInvoke(item -> context.view().delete(item))
                        .build()
        );
    }
}