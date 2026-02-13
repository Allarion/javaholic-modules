package de.javaholic.toolkit.iam.ui.adapter;

import de.javaholic.toolkit.iam.core.domain.Role;
import de.javaholic.toolkit.iam.core.spi.RoleStore;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

public interface RoleCrudStoreAdapter  extends CrudStore<Role, UUID>, RoleStore {
}
