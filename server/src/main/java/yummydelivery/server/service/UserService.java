package yummydelivery.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.UpdatePasswordDTO;
import yummydelivery.server.exceptions.InvalidCredentialsException;
import yummydelivery.server.exceptions.UserNotFoundException;
import yummydelivery.server.model.UserEntity;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.AuthenticationFacade;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AuthenticationFacade authenticationFacade, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
        this.passwordEncoder = passwordEncoder;
    }

    public void updateUserInfo(UpdatePasswordDTO userInfoDTO, Long userId) {
        authenticationFacade.checkIfUserIsAuthenticated();

        UserEntity user = userRepository
                .findById(userId).orElseThrow(() -> new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found"));

        if (!userInfoDTO.getNewPassword().equals(userInfoDTO.getRepeatNewPassword())) {
            throw new InvalidCredentialsException(HttpStatus.UNAUTHORIZED, "'New Password' does not match 'Repeat new password'");
        }
        if(!passwordEncoder.matches(userInfoDTO.getOldPassword(), user.getPassword())){
            throw new InvalidCredentialsException(HttpStatus.UNAUTHORIZED, "'Old Password' does not match your password");
        }
        user.setPassword(passwordEncoder.encode(userInfoDTO.getNewPassword()));
        userRepository.save(user);
    }
}
