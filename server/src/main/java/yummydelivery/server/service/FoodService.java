package yummydelivery.server.service;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.foodDTO.AddFoodDTO;
import yummydelivery.server.dto.foodDTO.FoodDTO;
import yummydelivery.server.dto.foodDTO.UpdateFoodDTO;
import yummydelivery.server.enums.FoodTypeEnum;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.*;
import yummydelivery.server.model.FoodEntity;
import yummydelivery.server.model.Product;
import yummydelivery.server.repository.ProductRepository;
import yummydelivery.server.security.AuthenticationFacade;


import java.util.List;
import java.util.stream.Collectors;


@Service
public class FoodService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationFacade authenticationFacade;


    public FoodService(ProductRepository productRepository, ModelMapper modelMapper,
                       AuthenticationFacade authenticationFacade) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.authenticationFacade = authenticationFacade;
    }

    public FoodDTO getFood(Long id) {
        Product product = productRepository
                .findById(id).orElseThrow(() -> new ProductNotFoundException(HttpStatus.NOT_FOUND, "Product not found"));
        FoodEntity foodEntityToReturn;
        if (product.getProductType().equals(ProductTypeEnum.FOOD)) {
            foodEntityToReturn = (FoodEntity) product;
        } else {
            throw new InvalidProductTypeException(HttpStatus.BAD_REQUEST, "Unexpected product type: " + product.getProductType());
        }
        return modelMapper.map(foodEntityToReturn, FoodDTO.class);
    }


    public void addFood(AddFoodDTO addFoodDTO) {

        authenticationFacade.checkIfUserIsAdmin();

        FoodEntity foodEntity = modelMapper.map(addFoodDTO, FoodEntity.class);
        foodEntity.setProductType(ProductTypeEnum.FOOD);
        productRepository.save(foodEntity);
    }

    public List<FoodDTO> getAllFoodsByType(String foodType) {
        FoodTypeEnum typeEnum = FoodTypeEnum.valueOf(foodType.toUpperCase());
        List<FoodEntity> foodsByType = productRepository
                .findAllByProductType(ProductTypeEnum.FOOD)
                .stream()
                .map(product -> (FoodEntity) product)
                .filter(foodEntity -> foodEntity.getFoodTypeEnum().equals(typeEnum))
                .toList();

        return foodsByType
                .stream()
                .map(f -> modelMapper.map(f, FoodDTO.class))
                .collect(Collectors.toList());
    }

    public void deleteFoodOrBeverage(Long id) {

        authenticationFacade.checkIfUserIsAdmin();

        if (!productRepository.existsById(id)) {
            throw new FoodNotFoundException(HttpStatus.NOT_FOUND, "Product with id " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    public void updateFood(Long id, UpdateFoodDTO updateFoodDTO) {

        authenticationFacade.checkIfUserIsAdmin();

        if (!productRepository.existsById(id)) {
            throw new FoodNotFoundException(HttpStatus.NOT_FOUND, "Product with id " + id + " not found");
        }

        Product product = productRepository.findById(id).get();
        if (!product.getProductType().equals(ProductTypeEnum.FOOD)) {
            throw new InvalidProductTypeException(HttpStatus.BAD_REQUEST, "To update food, product type must be 'Food'");
        }
        FoodEntity foodEntity;
        foodEntity = (FoodEntity) product;

        foodEntity.setName(updateFoodDTO.getName());
        foodEntity.setFoodTypeEnum(updateFoodDTO.getFoodTypeEnum());
        foodEntity.setPrice(updateFoodDTO.getPrice());
        foodEntity.setGrams(updateFoodDTO.getGrams());
        foodEntity.setImageURL(updateFoodDTO.getImageURL());
        foodEntity.setIngredients(updateFoodDTO.getIngredients());
        productRepository.save(foodEntity);
    }
}
