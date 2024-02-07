package yummydelivery.server.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
import yummydelivery.server.utils.CommonUtils;


import java.util.List;


@Service
@Slf4j
public class FoodService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationFacade authenticationFacade;
    private final CloudinaryService cloudinaryService;
    private final CommonUtils utils;

    public FoodService(ProductRepository productRepository, ModelMapper modelMapper,
                       AuthenticationFacade authenticationFacade, CloudinaryService cloudinaryService, CommonUtils utils) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.authenticationFacade = authenticationFacade;
        this.cloudinaryService = cloudinaryService;
        this.utils = utils;
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


    public void addFood(AddFoodDTO addFoodDTO, MultipartFile productImage) {
        authenticationFacade.checkIfUserIsAdmin();
        if (utils.productWithThisNameExist(addFoodDTO.getName())) {
            throw new IllegalArgumentException("Cannot use this product name. it's already assigned");
        }
        FoodEntity foodEntity = modelMapper.map(addFoodDTO, FoodEntity.class);

        if (productImage == null || productImage.isEmpty()) {
            String defaultProductImageURL = "https://res.cloudinary.com/dncjjyvqi/image/upload/v1707229014/YummyDeliveryImages/defaultProductImage.png";
            foodEntity.setImageURL(defaultProductImageURL);
            log.info("Product image is not provided. Default one is used");
        } else {
            cloudinaryService.validateImageFile(productImage);
            String imageUrl = cloudinaryService.uploadImage(productImage, addFoodDTO.getName());
            foodEntity.setImageURL(imageUrl);
            log.info("Product image is provided and uploaded successfully to Cloudinary");
        }
        foodEntity.setProductType(ProductTypeEnum.FOOD);
        productRepository.save(foodEntity);
    }

    public Page<FoodDTO> getAllFoodsByType(String foodType, int page) {
        if (page > 0) page -= 1;
        FoodTypeEnum typeEnum = FoodTypeEnum.valueOf(foodType.toUpperCase());
        Page<FoodEntity> foodsPage = productRepository
                .findAllByProductTypePageable(typeEnum, PageRequest.of(page, 6));

        List<FoodDTO> foodsByType = foodsPage
                .getContent()
                .stream()
                .map(foodEntity -> modelMapper.map(foodEntity, FoodDTO.class))
                .toList();

        return new PageImpl<>(foodsByType, foodsPage.getPageable(), foodsPage.getTotalElements());
    }

    public void deleteFoodOrBeverage(Long id) {
        authenticationFacade.checkIfUserIsAdmin();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new FoodNotFoundException(HttpStatus.NOT_FOUND, "Product with id " + id + " not found"));

        cloudinaryService.deleteProductImageFromCloudinary(product.getImageURL());
        productRepository.deleteById(id);
    }


    public void updateFood(Long id, UpdateFoodDTO updateFoodDTO, MultipartFile productImage) {
        authenticationFacade.checkIfUserIsAdmin();
        if (utils.productWithThisNameExist(updateFoodDTO.getName())) {
            throw new IllegalArgumentException("Cannot use this product name. it's already assigned");
        }
        if (!productRepository.existsById(id)) {
            throw new FoodNotFoundException(HttpStatus.NOT_FOUND, "Product with id " + id + " not found");
        }

        Product product = productRepository.findById(id).get();
        if (!product.getProductType().equals(ProductTypeEnum.FOOD)) {
            throw new InvalidProductTypeException(HttpStatus.BAD_REQUEST, "To update food, product type must be 'Food'");
        }
        FoodEntity foodEntity;
        foodEntity = (FoodEntity) product;

        if (productImage == null || productImage.isEmpty()) {
            updateFoodEntity(foodEntity, updateFoodDTO);
            log.info("Product image is not provided");
        } else {
            cloudinaryService.validateImageFile(productImage);
            String newImageURL = cloudinaryService.uploadImage(productImage, updateFoodDTO.getName());
            updateFoodEntity(foodEntity, updateFoodDTO, newImageURL);
            log.info("Product image is provided and uploaded successfully to Cloudinary");
        }

        productRepository.save(foodEntity);
    }

    private void updateFoodEntity(FoodEntity foodEntity, UpdateFoodDTO updateFoodDTO) {
        modelMapper.map(updateFoodDTO, foodEntity);
    }

    private void updateFoodEntity(FoodEntity foodEntity, UpdateFoodDTO updateFoodDTO, String newImageURL) {
        modelMapper.map(updateFoodDTO, foodEntity);
        foodEntity.setImageURL(newImageURL);
    }
}
