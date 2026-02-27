package de.javaholic.toolkit.ui.annotations;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UiSurface {

    Class<?> view(); // default ResourcePanel.class;

    Class<? extends UiActionProvider<?>> actions() default NoActions.class;

    final class NoActions implements UiActionProvider<Object> {
        @Override
        public List<ResourceAction<Object>> actions(UiSurfaceContext<Object> context) {
            return List.of();
        }

    }
}
