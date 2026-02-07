package de.javaholic.toolkit.iam.security.spring;

import de.javaholic.toolkit.iam.core.api.UserPrincipal;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface AuthenticationAdapter {

    boolean supports(Authentication auth);

    Optional<UserPrincipal> toUserPrincipal(Authentication auth);
}
