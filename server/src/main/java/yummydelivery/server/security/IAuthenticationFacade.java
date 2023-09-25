package yummydelivery.server.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


public interface IAuthenticationFacade {
    Authentication getAuthentication();

    void checkIfUserIsAuthorized();
}
