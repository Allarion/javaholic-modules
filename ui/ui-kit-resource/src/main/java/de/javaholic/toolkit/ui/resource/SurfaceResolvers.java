package de.javaholic.toolkit.ui.resource;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import de.javaholic.toolkit.introspection.BeanIntrospector;
import de.javaholic.toolkit.ui.annotations.UiSurface;
import de.javaholic.toolkit.ui.api.ResourceAction;
import de.javaholic.toolkit.ui.api.UiActionProvider;
import de.javaholic.toolkit.ui.api.UiSurfaceContext;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

final class SurfaceResolvers {

    private SurfaceResolvers() {
    }

    static <T> List<ResourceAction<T>> resolveActions(Class<T> dtoType, UiSurfaceContext<T> context) {
        Objects.requireNonNull(dtoType, "dtoType");
        Objects.requireNonNull(context, "context");

        UiSurface surface = BeanIntrospector.inspect(dtoType).type().getAnnotation(UiSurface.class);
        if (surface == null || surface.actions() == Void.class) {
            return List.of();
        }

        Class<?> configuredProviderType = surface.actions();
        if (!UiActionProvider.class.isAssignableFrom(configuredProviderType)) {
            throw new IllegalStateException(
                    "Configured UiSurface.actions type " + configuredProviderType.getName()
                            + " does not implement UiActionProvider."
            );
        }

        @SuppressWarnings("unchecked")
        Class<? extends UiActionProvider<?>> providerType =
                (Class<? extends UiActionProvider<?>>) configuredProviderType;

        UiActionProvider<T> provider = resolveProvider(providerType);
        List<ResourceAction<T>> actions = provider.actions(context);
        if (actions == null) {
            return List.of();
        }
        return List.copyOf(actions);
    }

    @SuppressWarnings("unchecked")
    private static <T> UiActionProvider<T> resolveProvider(Class<? extends UiActionProvider<?>> providerType) {
        UI ui = UI.getCurrent();
        if (ui != null) {
            Instantiator instantiator = Instantiator.get(ui);
            if (instantiator != null) {
                return (UiActionProvider<T>) instantiator.getOrCreate(providerType);
            }
        }
        return instantiateProvider(providerType);
    }

    @SuppressWarnings("unchecked")
    private static <T> UiActionProvider<T> instantiateProvider(Class<? extends UiActionProvider<?>> providerType) {
        try {
            Constructor<? extends UiActionProvider<?>> constructor = providerType.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (UiActionProvider<T>) constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to instantiate UiActionProvider " + providerType.getName(), e);
        }
    }
}
