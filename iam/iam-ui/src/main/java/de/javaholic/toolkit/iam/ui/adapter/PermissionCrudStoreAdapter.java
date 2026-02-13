package de.javaholic.toolkit.iam.ui.adapter;

import de.javaholic.toolkit.iam.core.domain.Permission;
import de.javaholic.toolkit.iam.core.spi.PermissionStore;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

public interface PermissionCrudStoreAdapter extends CrudStore<Permission, UUID>, PermissionStore {}
