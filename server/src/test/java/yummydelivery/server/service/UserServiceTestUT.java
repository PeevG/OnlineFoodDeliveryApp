package yummydelivery.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import yummydelivery.server.dto.UpdatePasswordDTO;
import yummydelivery.server.exceptions.InvalidCredentialsException;
import yummydelivery.server.model.UserEntity;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.AuthenticationFacade;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTestUT {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void updateUserPassword_Success() {
        UserEntity currentUser = new UserEntity();
        currentUser.setEmail("user@email.com");
        currentUser.setPassword(passwordEncoder.encode("password"));

        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setOldPassword("password");
        dto.setNewPassword("newPassword");
        dto.setRepeatNewPassword("newPassword");

        String newPassEncoded = passwordEncoder.encode(dto.getNewPassword());
        Authentication authenticationMock = mock();

        when(authenticationMock.getName()).thenReturn(currentUser.getEmail());
        when(authenticationFacade.getAuthentication()).thenReturn(authenticationMock);
        when(passwordEncoder.matches(dto.getOldPassword(), currentUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(dto.getNewPassword())).thenReturn(newPassEncoded);
        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));

        userService.updateUserPassword(dto);

        verify(userRepository, times(1)).save(currentUser);
    }

    @Test
    public void updateUserPassword_NewPasswordAndRepeatNewPassword_NotMatch_ThrowException() {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setNewPassword("blabla");
        dto.setRepeatNewPassword("DifferentPassword");

        UserEntity currentUser = new UserEntity();
        currentUser.setEmail("user@email.com");
        currentUser.setPassword(passwordEncoder.encode("password"));

        Authentication authenticationMock = mock();
        when(authenticationMock.getName()).thenReturn(currentUser.getEmail());
        when(authenticationFacade.getAuthentication()).thenReturn(authenticationMock);
        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));

        assertThrows(InvalidCredentialsException.class, () -> userService.updateUserPassword(dto));
        verify(userRepository, never()).save(currentUser);
    }

    @Test
    public void updateUserPassword_DtoOldPasswordDoesNotMatchCurrentUserPassword_ThrowException() {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setOldPassword("wrong old password");
        dto.setNewPassword("newPassword");
        dto.setRepeatNewPassword("newPassword");

        UserEntity currentUser = new UserEntity();
        currentUser.setEmail("user@email.com");
        currentUser.setPassword(passwordEncoder.encode("old password"));

        Authentication authenticationMock = mock();
        when(authenticationMock.getName()).thenReturn(currentUser.getEmail());
        when(authenticationFacade.getAuthentication()).thenReturn(authenticationMock);
        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches(dto.getOldPassword(), currentUser.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.updateUserPassword(dto));
        verify(userRepository, never()).save(currentUser);
    }
}