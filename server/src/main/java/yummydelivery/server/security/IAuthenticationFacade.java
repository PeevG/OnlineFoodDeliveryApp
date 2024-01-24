package yummydelivery.server.security;

import org.springframework.security.core.Authentication;


public interface IAuthenticationFacade {
    Authentication getAuthentication();

    void checkIfUserIsAdmin();
    void checkIfUserIsAuthenticated();
}
