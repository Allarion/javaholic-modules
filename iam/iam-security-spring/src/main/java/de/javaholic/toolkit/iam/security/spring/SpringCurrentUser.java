package de.javaholic.toolkit.iam.security.spring;

import de.javaholic.toolkit.iam.core.api.CurrentUser;
import de.javaholic.toolkit.iam.core.api.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SpringCurrentUser implements CurrentUser {

    private final List<AuthenticationAdapter> adapters;

    public SpringCurrentUser(List<AuthenticationAdapter> adapters) {
        this.adapters = List.copyOf(Objects.requireNonNull(adapters, "adapters"));
    }

    @Override
    public Optional<UserPrincipal> get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return Optional.empty();
        }
        for (AuthenticationAdapter adapter : adapters) {
            if (adapter.supports(auth)) {
                return adapter.toUserPrincipal(auth);
            }
        }
        return Optional.empty();
    }
}
