package de.javaholic.toolkit.ui.api;

import java.util.Optional;
/**
 * Context object passed to UiActionProvider and potentially future
 * surface-level integrations.
 *
 * <h2>Current Responsibility</h2>
 * - Provides access to the DTO type
 * - Provides access to the instantiated surface view
 *
 *
 * <h2>Architectural Position</h2>
 *
 * UiSurfaceContext is intentionally minimal.
 * It must NOT:
 * - Depend on concrete UI implementations (Vaadin, etc.)
 * - Expose internal view mechanics
 * - Contain business logic
 *
 *
 * <h2>TODO: Planned Extensions</h2>
 *
 * In the future this context may provide:
 * <ul>
 * <li> Current user (for permission-aware action providers)</li>
 * <li> election state abstraction (example, see SelectionState<T>)</li>
 * <li> Routing information (e.g. /users/{id}/roles, /users?mode=compact)</li>
 * <li> View role/type information (several @UiSurfaces/DTO: LIST, DETAIL.)</li>
 * <li> Surface lifecycle hooks (SurfaceLifecycle<T>.onInit, onSelectionChange, onRefresh, onDispose)</li>
 *</ul>
 * The goal is to evolve towards a stable surface contract
 * without coupling foundation modules to UI implementations.
 */
public interface UiSurfaceContext<T> {

    Class<T> dtoType();

    Optional<T> currentSelection();

    void refresh();

    ResourceView<T> view();

    // TODO: (later version) add fields: PermissionChecker, CurrentUser, Store
}

//interface SelectionState<T> {
//
//    Set<T> selected();
//
//    boolean isEmpty();
//
//    Optional<T> single();
//
//    void clear();
//
//    void set(Set<T> selection);
////////////////////77
//public enum SurfaceRole {
//    DATASET,
//    DETAIL,
//    TREE,
//    GRAPH,
//    PREVIEW
//}
//then
//@UiSurface(view = FormsResourceView.class, role = SurfaceRole.DATASET)
//@UiSurface(view = UserDetailView.class, role = SurfaceRole.DETAIL)