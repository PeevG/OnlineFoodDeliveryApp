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
import yummydelivery.server.model.FoodEntity;
import yummydelivery.server.repository.FoodRepository;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.AuthenticationFacade;


import java.util.List;
import java.util.stream.Collectors;


@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;

    public FoodService(FoodRepository foodRepository, ModelMapper modelMapper,
                       AuthenticationFacade authenticationFacade, UserRepository userRepository) {
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

        authenticationFacade.checkIfUserIsAuthorized();

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

        authenticationFacade.checkIfUserIsAuthorized();

        if (!foodRepository.existsById(id)) {
            throw new FoodNotFoundException(HttpStatus.NOT_FOUND, "Food with id " + id + " not found");
        }
        foodRepository.deleteById(id);
    }

    public void updateFood(Long id, UpdateFoodDTO updateFoodDTO) {

        authenticationFacade.checkIfUserIsAuthorized();

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
}
