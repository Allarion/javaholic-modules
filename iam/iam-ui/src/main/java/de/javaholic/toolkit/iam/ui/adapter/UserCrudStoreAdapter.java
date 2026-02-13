package de.javaholic.toolkit.iam.ui.adapter;

import de.javaholic.toolkit.iam.core.domain.User;
import de.javaholic.toolkit.iam.core.spi.UserStore;
import de.javaholic.toolkit.persistence.core.CrudStore;

import java.util.UUID;

public interface UserCrudStoreAdapter extends CrudStore<User, UUID>, UserStore {
}
