package yummydelivery.server.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import yummydelivery.server.exceptions.UnauthorizedException;
import yummydelivery.server.exceptions.UserNotFoundException;
import yummydelivery.server.model.UserEntity;
import yummydelivery.server.repository.UserRepository;

@Service
public class AuthenticationFacade implements IAuthenticationFacade {
    private final UserRepository userRepository;

    public AuthenticationFacade(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public void checkIfUserIsAuthorized() {
        String username = getAuthentication().getName();
        UserEntity currentUser = userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found"));
        String userRole = currentUser.getRoles().stream().map(r -> r.getName().name()).findFirst().get();
        if (!userRole.equals("ADMIN")) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "You are not authorized for this operation");
        }
    }
}
