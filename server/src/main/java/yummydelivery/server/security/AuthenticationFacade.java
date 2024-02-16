package yummydelivery.server.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import yummydelivery.server.exceptions.UnauthorizedException;

@Component
public class AuthenticationFacade implements IAuthenticationFacade {


    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    @Override
    public void checkIfUserIsAuthenticated() {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
    }
}
