package yummydelivery.server.service;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.SignUpDTO;
import yummydelivery.server.enums.RoleEnum;
import yummydelivery.server.exceptions.RoleNotFoundException;
import yummydelivery.server.model.AddressEntity;
import yummydelivery.server.model.RoleEntity;
import yummydelivery.server.model.UserEntity;
import yummydelivery.server.repository.RoleRepository;
import yummydelivery.server.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void signUpUser(SignUpDTO signUpDTO) {

        RoleEntity customerRole = roleRepository.findByName(RoleEnum.CUSTOMER)
                .orElseThrow(() -> new RoleNotFoundException(HttpStatus.INTERNAL_SERVER_ERROR, "No Default User Role in the database"));

        AddressEntity userAddress = modelMapper.map(signUpDTO.getAddress(), AddressEntity.class);

        UserEntity newUser = modelMapper.map(signUpDTO, UserEntity.class);
        newUser.setAddresses(List.of(userAddress));
        //Todo encrypt user password
        newUser.setRoles(List.of(customerRole));

        userRepository.save(newUser);
    }

}
