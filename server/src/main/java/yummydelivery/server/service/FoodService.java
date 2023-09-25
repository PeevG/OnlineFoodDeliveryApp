package yummydelivery.server.service;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.foodDTO.AddFoodDTO;
import yummydelivery.server.dto.foodDTO.FoodDTO;
import yummydelivery.server.dto.foodDTO.UpdateFoodDTO;
import yummydelivery.server.enums.FoodTypeEnum;
import yummydelivery.server.exceptions.ApiException;
import yummydelivery.server.exceptions.FoodNotFoundException;
import yummydelivery.server.exceptions.UnauthorizedException;
import yummydelivery.server.exceptions.UserNotFoundException;
import yummydelivery.server.model.FoodEntity;
import yummydelivery.server.model.UserEntity;
import yummydelivery.server.repository.FoodRepository;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.IAuthenticationFacade;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final ModelMapper modelMapper;
    private final IAuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;

    public FoodService(FoodRepository foodRepository, ModelMapper modelMapper, IAuthenticationFacade authenticationFacade, UserRepository userRepository) {
        this.foodRepository = foodRepository;
        this.modelMapper = modelMapper;
        this.authenticationFacade = authenticationFacade;
        this.userRepository = userRepository;
    }

    public FoodDTO getFood(Long id) {
        FoodEntity foodEntity = foodRepository
                .findById(id)
                .orElseThrow(() -> new FoodNotFoundException(HttpStatus.NOT_FOUND, "Food with id " + id + " not found."));
        return modelMapper.map(foodEntity, FoodDTO.class);
    }

    public void addFood(AddFoodDTO addFoodDTO) {

        checkIfUserIsAuthorized();

        FoodEntity foodEntity = modelMapper.map(addFoodDTO, FoodEntity.class);
        foodRepository.save(foodEntity);
    }

    public List<FoodDTO> getAllFoodsByType(String foodType) {
        FoodTypeEnum typeEnum = FoodTypeEnum.valueOf(foodType.toUpperCase());

        List<FoodEntity> allByFoodType = foodRepository.findAllByFoodTypeEnum(typeEnum)
                .orElseThrow(() -> new ApiException(HttpStatus.OK, "Invalid foodType"));

        return allByFoodType
                .stream()
                .map(f -> modelMapper.map(f, FoodDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteFood(Long id) {

        checkIfUserIsAuthorized();

        if (!foodRepository.existsById(id)) {
            throw new FoodNotFoundException(HttpStatus.NOT_FOUND, "Food with id " + id + " not found");
        }
        foodRepository.deleteById(id);
    }

    public void updateFood(Long id, UpdateFoodDTO updateFoodDTO) {

        checkIfUserIsAuthorized();

        if (!foodRepository.existsById(id)) {
            throw new FoodNotFoundException(HttpStatus.NOT_FOUND, "Food with id " + id + " not found");
        }
        FoodEntity foodEntity = foodRepository.findById(id).get();
        foodEntity.setName(updateFoodDTO.getName());
        foodEntity.setFoodTypeEnum(updateFoodDTO.getFoodTypeEnum());
        foodEntity.setPrice(updateFoodDTO.getPrice());
        foodEntity.setWeight(updateFoodDTO.getWeight());
        foodEntity.setImageURL(updateFoodDTO.getImageURL());
        foodEntity.setIngredients(updateFoodDTO.getIngredients());
        foodRepository.save(foodEntity);
    }

    private void checkIfUserIsAuthorized() {
        String username = authenticationFacade.getAuthentication().getName();
        UserEntity currentUser = userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found"));
        String userRole = currentUser.getRoles().stream().map(r -> r.getName().name()).findFirst().get();
        if (!userRole.equals("ADMIN")) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "You are not authorized for this operation");
        }
    }
}
