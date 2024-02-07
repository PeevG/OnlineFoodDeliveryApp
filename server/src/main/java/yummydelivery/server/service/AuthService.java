package yummydelivery.server.service;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.EmailDetails;
import yummydelivery.server.dto.SignInDTO;
import yummydelivery.server.dto.SignUpDTO;
import yummydelivery.server.enums.RoleEnum;
import yummydelivery.server.exceptions.EmailAlreadyTakenException;
import yummydelivery.server.exceptions.InvalidCredentialsException;
import yummydelivery.server.exceptions.RoleNotFoundException;
import yummydelivery.server.model.AddressEntity;
import yummydelivery.server.model.RoleEntity;
import yummydelivery.server.model.UserEntity;
import yummydelivery.server.repository.AddressRepository;
import yummydelivery.server.repository.RoleRepository;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.JwtTokenProvider;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, AddressRepository addressRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
    }

    @Transactional
    public void signUpUser(SignUpDTO signUpDTO) {

        if (userRepository.existsByEmail(signUpDTO.getEmail())) {
            throw new EmailAlreadyTakenException(HttpStatus.BAD_REQUEST, "Email is already in use");
        }

        RoleEntity customerRole = roleRepository.findByName(RoleEnum.CUSTOMER)
                .orElseThrow(() -> new RoleNotFoundException(HttpStatus.INTERNAL_SERVER_ERROR, "No Default User Role in the database"));

        AddressEntity userAddress = mapDtoAddressInfoToAddress(signUpDTO);
        UserEntity newUser = mapDtoToUser(signUpDTO, customerRole, userAddress);

        userRepository.save(newUser);

        emailService.sendEmail(EmailDetails.builder()
                .messageBody("Successful registration. You can login can login now.")
                .recipient(newUser.getEmail())
                .subject("Registration Success")
                .build());
    }

    public String signInUser(SignInDTO signInDTO) {
        if (!userRepository.existsByEmail(signInDTO.getEmail())) {
            throw new InvalidCredentialsException(HttpStatus.UNAUTHORIZED, "Incorrect email!");
        }

        Optional<UserEntity> byEmail = userRepository.findByEmail(signInDTO.getEmail());

        if (!passwordEncoder.matches(signInDTO.getPassword(), byEmail.get().getPassword())) {
            throw new InvalidCredentialsException(HttpStatus.UNAUTHORIZED, "Incorrect password!");
        }

        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(signInDTO.getEmail(), signInDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateJwtToken(authentication);
    }

    private UserEntity mapDtoToUser(SignUpDTO signUpDTO, RoleEntity customerRole, AddressEntity userAddress) {
        UserEntity newUser = new UserEntity();
        newUser.setRoles(Set.of(customerRole));
        newUser.setAddresses(List.of(userAddress));
        newUser.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        newUser.setEmail(signUpDTO.getEmail());
        newUser.setFirstName(signUpDTO.getFirstName());
        newUser.setLastName(signUpDTO.getLastName());
        return newUser;
    }

    private AddressEntity mapDtoAddressInfoToAddress(SignUpDTO signUpDTO) {
        AddressEntity userAddress = new AddressEntity();
        userAddress.setCity(signUpDTO.getCity());
        userAddress.setPhoneNumber(signUpDTO.getPhoneNumber());
        userAddress.setStreetName(signUpDTO.getStreetName());
        userAddress.setStreetNumber(signUpDTO.getStreetNumber());
        addressRepository.save(userAddress);
        return userAddress;
    }
}
