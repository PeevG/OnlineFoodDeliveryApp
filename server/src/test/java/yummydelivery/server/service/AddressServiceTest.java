package yummydelivery.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import yummydelivery.server.dto.AddressDTO;
import yummydelivery.server.dto.view.AddressView;
import yummydelivery.server.exceptions.AddressNotFoundException;
import yummydelivery.server.exceptions.UnauthorizedException;
import yummydelivery.server.model.AddressEntity;
import yummydelivery.server.model.UserEntity;
import yummydelivery.server.repository.AddressRepository;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.AuthenticationFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private UserService userService;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateAddress_AddressNotFound() {
        AddressDTO addressDTO = new AddressDTO();
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressService.updateAddress(addressDTO, 1L));
    }

    @Test
    void updateAddress_Unauthorized() {
        AddressDTO dto = new AddressDTO();

        UserEntity user = new UserEntity();
        user.setEmail("user@gmail.com");
        user.setId(1L);

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setId(1L);

        Authentication authentication = mock(Authentication.class);

        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(addressRepository.findById(1L)).thenReturn(Optional.of(addressEntity));

        assertThrows(UnauthorizedException.class, () -> addressService.updateAddress(dto, 1L));
    }

    @Test
    void updateAddress_Success() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCity("newCity");
        addressDTO.setStreetNumber("12");
        addressDTO.setPhoneNumber("012345678");
        addressDTO.setStreetName("Ulichka");

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setId(1L);

        List<AddressEntity> userAddresses = new ArrayList<>();
        userAddresses.add(addressEntity);

        UserEntity user = new UserEntity();
        user.setEmail("current@email.com");
        user.setAddresses(userAddresses);

        Authentication authentication = mock(Authentication.class);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(addressEntity));
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("current@email.com");
        when(addressRepository.getAddressEntitiesByUsername(anyString())).thenReturn(userAddresses);

        AddressDTO result = addressService.updateAddress(addressDTO, 1L);

        assertNotNull(result);
        assertEquals(addressDTO, result);
        verify(addressRepository, times(1)).save(addressEntity);
    }

    @Test
    public void updateAddress_AddressNotFound_ExceptionThrown() {
        UserEntity user = new UserEntity();
        user.setEmail("currentUser@gmail.com");
        user.setId(1L);

        AddressDTO addressDTO = new AddressDTO();

        Authentication authentication = mock();
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(userService.getCurrentUserByUsername()).thenReturn(user);
        when(addressRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> addressService.updateAddress(addressDTO, 2L));
    }

    @Test
    public void getUserAddresses_Success() {
        UserEntity user = new UserEntity();
        user.setEmail("currentUser@gmail.com");
        user.setId(1L);

        AddressEntity userAddress = new AddressEntity();
        userAddress.setId(1L);
        userAddress.setCity("Sofia");

        AddressEntity userAddress2 = new AddressEntity();
        userAddress2.setId(2L);
        userAddress2.setCity("Varna");

        List<AddressEntity> userAddresses = new ArrayList<>();
        userAddresses.add(userAddress);
        userAddresses.add(userAddress2);

        user.setAddresses(userAddresses);

        Authentication authentication = mock();
        PageRequest pageRequest = PageRequest.of(0, 6);


        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(addressRepository.findAllAddressesByUserId(1L, pageRequest)).thenReturn(new PageImpl(userAddresses));
        when(authentication.getName()).thenReturn("currentUser@gmail.com");
        when(userService.getCurrentUserByUsername()).thenReturn(user);

        Page<AddressView> result = addressService.getUserAddresses(1);

        assertNotNull(result);
        assertEquals(result.getTotalElements(), userAddresses.size());
    }

    @Test
    void addNewAddress_Success() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCity("newCity");
        addressDTO.setStreetNumber("12");
        addressDTO.setPhoneNumber("012345678");
        addressDTO.setStreetName("Ulichka");

        AddressEntity newAddress = new AddressEntity();
        newAddress.setId(15L);
        newAddress.setCity("newCity");
        newAddress.setStreetNumber("12");
        newAddress.setPhoneNumber("012345678");
        newAddress.setStreetName("Ulichka");

        List<AddressEntity> userAddresses = new ArrayList<>();
        userAddresses.add(newAddress);

        UserEntity user = new UserEntity();
        user.setEmail("current@email.com");
        user.setAddresses(userAddresses);

        Authentication authentication = mock(Authentication.class);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(newAddress));
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("current@email.com");
        when(userService.getCurrentUserByUsername()).thenReturn(user);
        when(modelMapper.map(addressDTO, AddressEntity.class)).thenReturn(newAddress);
        when(addressRepository.getAddressEntitiesByUsername(anyString())).thenReturn(userAddresses);

        AddressDTO result = addressService.addNewAddress(addressDTO);

        assertNotNull(result);
        assertEquals(addressDTO.getCity(), newAddress.getCity());
        assertEquals(addressDTO.getStreetName(), newAddress.getStreetName());
        assertEquals(addressDTO.getPhoneNumber(), newAddress.getPhoneNumber());
        assertEquals(addressDTO.getStreetNumber(), newAddress.getStreetNumber());

        verify(addressRepository, times(1)).save(newAddress);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void deleteAddress_Success() {
        AddressEntity newAddress = new AddressEntity();
        newAddress.setId(10L);
        AddressEntity secondAddress = new AddressEntity();
        secondAddress.setId(11L);

        List<AddressEntity> userAddresses = new ArrayList<>();
        userAddresses.add(newAddress);
        userAddresses.add(secondAddress);

        UserEntity currentUser = new UserEntity();
        currentUser.setEmail("current@email.com");
        currentUser.setAddresses(userAddresses);

        Authentication authentication = mock(Authentication.class);
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUser.getEmail());
        when(addressRepository.findById(10L)).thenReturn(Optional.of(newAddress));
        when(userService.getCurrentUserByUsername()).thenReturn(currentUser);
        when(addressRepository.getAddressEntitiesByUsername(currentUser.getEmail())).thenReturn(userAddresses);

        addressService.deleteAddress(10L);

        assertEquals(1, userAddresses.size());
        assertEquals(userAddresses.get(0), secondAddress);
        assertEquals(userAddresses.get(0).getId(), secondAddress.getId());
        verify(addressRepository, times(1)).delete(newAddress);
        verify(userRepository, times(1)).save(currentUser);
    }

    @Test
    public void deleteAddress_ThatIsNotYour_ExceptionThrown() {
        AddressEntity newAddress = new AddressEntity();
        newAddress.setId(10L);

        AddressEntity wrongAddressToDelete = new AddressEntity();
        wrongAddressToDelete.setId(15L);

        List<AddressEntity> userAddresses = new ArrayList<>();
        userAddresses.add(newAddress);

        UserEntity currentUser = new UserEntity();
        currentUser.setEmail("current@email.com");
        currentUser.setAddresses(userAddresses);

        Authentication authentication = mock(Authentication.class);
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUser.getEmail());
        when(addressRepository.findById(15L)).thenReturn(Optional.of(wrongAddressToDelete));
        when(userService.getCurrentUserByUsername()).thenReturn(currentUser);
        when(addressRepository.getAddressEntitiesByUsername(currentUser.getEmail())).thenReturn(userAddresses);

        assertThrows(UnauthorizedException.class,() -> addressService.deleteAddress(15L));
    }
}