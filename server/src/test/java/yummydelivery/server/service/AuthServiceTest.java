package yummydelivery.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import yummydelivery.server.dto.SignInDTO;
import yummydelivery.server.dto.SignUpDTO;
import yummydelivery.server.enums.RoleEnum;
import yummydelivery.server.exceptions.EmailAlreadyTakenException;
import yummydelivery.server.exceptions.InvalidCredentialsException;
import yummydelivery.server.exceptions.RoleNotFoundException;
import yummydelivery.server.model.RoleEntity;
import yummydelivery.server.model.UserEntity;
import yummydelivery.server.repository.AddressRepository;
import yummydelivery.server.repository.RoleRepository;
import yummydelivery.server.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private AddressRepository addressRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void signUpUser_Success() {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setEmail("qwerty@example.com");
        signUpDTO.setPassword("SomePassword");
        signUpDTO.setFirstName("Peter");
        signUpDTO.setLastName("Milanov");
        signUpDTO.setCity("Sofia");
        signUpDTO.setPhoneNumber("1234567890");
        signUpDTO.setStreetName("CoolStreetName");
        signUpDTO.setStreetNumber("13");

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName(RoleEnum.CUSTOMER);

        when(userRepository.existsByEmail(signUpDTO.getEmail())).thenReturn(false);
        when(roleRepository.findByName(RoleEnum.CUSTOMER)).thenReturn(Optional.of(roleEntity));

        authService.signUpUser(signUpDTO);

        verify(userRepository, times(1)).save(any());
        verify(emailService, times(1)).sendEmail(any());
    }

    @Test
    public void signUpUser_EmailAlreadyTaken_ExceptionThrown() {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setEmail("TakenEmail@example.com");

        when(userRepository.existsByEmail(signUpDTO.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyTakenException.class, () -> authService.signUpUser(signUpDTO));
    }

    @Test
    public void signUpUser_RoleNotFound_ExceptionThrown() {
        SignUpDTO signUpDTO = new SignUpDTO();
        signUpDTO.setEmail("SomeEmail@example.com");
        RoleEntity role = new RoleEntity();
        role.setName(RoleEnum.CUSTOMER);

        when(userRepository.existsByEmail(signUpDTO.getEmail())).thenReturn(false);
        when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> authService.signUpUser(signUpDTO));
    }

    @Test
    public void signInUser_InvalidEmail_ExceptionThrown() {
        SignInDTO signInDTO = new SignInDTO();
        signInDTO.setEmail("SomeEmail@example.com");

        when(userRepository.existsByEmail(signInDTO.getEmail())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.signInUser(signInDTO));
    }

    @Test
    public void signInUser_IncorrectPassword_ExceptionThrown() {
        SignInDTO signIn = new SignInDTO();
        signIn.setEmail("Email@something.com");
        signIn.setPassword("notCorrect");

        UserEntity user = new UserEntity();

        when(userRepository.findByEmail(signIn.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.signInUser(signIn));
    }
}