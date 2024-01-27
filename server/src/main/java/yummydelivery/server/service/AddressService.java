package yummydelivery.server.service;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.AddressDTO;
import yummydelivery.server.dto.AddressView;
import yummydelivery.server.exceptions.AddressNotFoundException;
import yummydelivery.server.exceptions.UnauthorizedException;
import yummydelivery.server.exceptions.UserNotFoundException;
import yummydelivery.server.model.AddressEntity;
import yummydelivery.server.model.UserEntity;
import yummydelivery.server.repository.AddressRepository;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.AuthenticationFacade;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final ModelMapper modelMapper;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository, AuthenticationFacade authenticationFacade, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
        this.modelMapper = modelMapper;
    }

    public List<AddressView> getUserAddresses() {
        authenticationFacade.checkIfUserIsAuthenticated();

        String userName = authenticationFacade.getAuthentication().getName();
        UserEntity user = userRepository
                .findByEmail(userName).orElseThrow(() -> new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found"));

        return addressRepository.findAllAddressesByUserId(user.getId());
    }


    public AddressDTO addNewAddress(AddressDTO addressDTO) {
        authenticationFacade.checkIfUserIsAuthenticated();

        String username = authenticationFacade.getAuthentication().getName();
        UserEntity user = userRepository
                .findByEmail(username).orElseThrow(() -> new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found"));

        AddressEntity newAddress = modelMapper.map(addressDTO, AddressEntity.class);
        addressRepository.save(newAddress);

        List<AddressEntity> userAddresses = addressRepository.getAddressEntitiesByUsername(username);
        userAddresses.add(newAddress);
        user.setAddresses(userAddresses);
        userRepository.save(user);
        return addressDTO;
    }

    public AddressDTO updateAddress(AddressDTO addressDTO, Long addressId) {
        authenticationFacade.checkIfUserIsAuthenticated();

        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(HttpStatus.NOT_FOUND, "Address with id " + addressId + " not found"));

        if (addressNotBelongToYou(address)) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "You are not allowed to delete an address that isn't yours");
        }
        modelMapper.map(addressDTO, address);

        addressRepository.save(address);
        return addressDTO;
    }

    public void deleteAddress(Long addressId) {
        authenticationFacade.checkIfUserIsAuthenticated();

        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(HttpStatus.NOT_FOUND, "Address with id " + addressId + " not found"));
        if (addressNotBelongToYou(address)) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "You are not allowed to delete an address that isn't yours");
        }
        String username = authenticationFacade.getAuthentication().getName();
        List<AddressEntity> userAddresses = addressRepository
                .getAddressEntitiesByUsername(username);
        userAddresses.remove(address);
        UserEntity user = userRepository
                .findByEmail(username).orElseThrow(() -> new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found"));
        user.setAddresses(userAddresses);
        userRepository.save(user);
        addressRepository.delete(address);
    }

    private boolean addressNotBelongToYou(AddressEntity address) {
        String userName = authenticationFacade.getAuthentication().getName();
        List<AddressEntity> userAddresses = addressRepository.getAddressEntitiesByUsername(userName);
        return !userAddresses.contains(address);
    }
}
