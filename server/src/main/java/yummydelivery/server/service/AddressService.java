package yummydelivery.server.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.AddressDTO;
import yummydelivery.server.dto.view.AddressView;
import yummydelivery.server.exceptions.AddressNotFoundException;
import yummydelivery.server.exceptions.UnauthorizedException;
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
    private final UserService userService;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository, AuthenticationFacade authenticationFacade, ModelMapper modelMapper, UserService userService) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    public Page<AddressView> getUserAddresses(int page) {
        authenticationFacade.checkIfUserIsAuthenticated();

        UserEntity user = userService.getCurrentUserByUsername();
        if(page > 0) page -= 1;
        return addressRepository.findAllAddressesByUserId(user.getId(), PageRequest.of(page, 6));
    }


    public AddressDTO addNewAddress(AddressDTO addressDTO) {
        authenticationFacade.checkIfUserIsAuthenticated();

        UserEntity user = userService.getCurrentUserByUsername();

        AddressEntity newAddress = modelMapper.map(addressDTO, AddressEntity.class);
        addressRepository.save(newAddress);

        List<AddressEntity> userAddresses = addressRepository.getAddressEntitiesByUsername(user.getEmail());
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
        UserEntity user = userService.getCurrentUserByUsername();
        user.setAddresses(userAddresses);
        userRepository.save(user);
        addressRepository.delete(address);
    }

    protected boolean addressNotBelongToYou(AddressEntity address) {
        String userName = authenticationFacade.getAuthentication().getName();
        List<AddressEntity> userAddresses = addressRepository.getAddressEntitiesByUsername(userName);
        return !userAddresses.contains(address);
    }
}
