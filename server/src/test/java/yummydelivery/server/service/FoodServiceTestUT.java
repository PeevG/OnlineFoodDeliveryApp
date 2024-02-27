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
import org.springframework.web.multipart.MultipartFile;
import yummydelivery.server.dto.foodDTO.AddFoodDTO;
import yummydelivery.server.dto.foodDTO.FoodDTO;
import yummydelivery.server.dto.foodDTO.UpdateFoodDTO;
import yummydelivery.server.enums.FoodTypeEnum;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.InvalidProductTypeException;
import yummydelivery.server.exceptions.ProductNotFoundException;
import yummydelivery.server.model.BeverageEntity;
import yummydelivery.server.model.FoodEntity;
import yummydelivery.server.model.Product;
import yummydelivery.server.repository.ProductRepository;
import yummydelivery.server.utils.CommonUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FoodServiceTestUT {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CommonUtils utils;
    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private FoodService foodService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getFoodById_ProductNotFound_Throw_ProductNotFoundException() {
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> foodService.getFoodById(productId));
    }

    @Test
    public void getFoodById_InvalidProductType_Throw_InvalidProductTypeException() {
        Long productId = 1L;
        Product beverage = new BeverageEntity();
        beverage.setId(productId);
        beverage.setProductType(ProductTypeEnum.BEVERAGE);

        when(productRepository.findById(productId)).thenReturn(Optional.of(beverage));

        assertThrows(InvalidProductTypeException.class, () -> foodService.getFoodById(productId));
    }

    @Test
    public void getFoodById_ReturnFoodDto() {
        FoodEntity food = new FoodEntity();
        food.setId(1L);
        food.setName("Pizza");
        food.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        food.setProductType(ProductTypeEnum.FOOD);

        FoodDTO expectedDTO = new FoodDTO();

        when(productRepository.findById(1L)).thenReturn(Optional.of(food));
        when(modelMapper.map(food, FoodDTO.class)).thenReturn(expectedDTO);

        FoodDTO result = foodService.getFoodById(1L);

        assertEquals(expectedDTO, result);
    }

    @Test
    public void addFood_NameExists_Throw_IllegalArgumentException() {
        AddFoodDTO foodDto = new AddFoodDTO();
        foodDto.setName("Exists");

        when(utils.productWithThisNameExist(foodDto.getName())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> foodService.addFood(foodDto, null));
        verify(productRepository, never()).save(any());
    }

    @Test
    public void addFood_WhenProductWithSameNameDoesNotExist_ShouldSaveFoodEntityWithDefaultImage() {

        AddFoodDTO addFoodDTO = new AddFoodDTO();
        addFoodDTO.setName("New Pizza");
        addFoodDTO.setPrice(10.00);
        when(utils.productWithThisNameExist("New Pizza")).thenReturn(false);
        when(modelMapper.map(addFoodDTO, FoodEntity.class)).thenReturn(new FoodEntity());

        foodService.addFood(addFoodDTO, null);

        verify(cloudinaryService, never()).uploadImage(any(), anyString());
        verify(productRepository, times(1)).save(any());
    }

    @Test
    public void addFood_WhenProductImageProvided_ShouldSaveFoodEntityWithUploadedImage() {
        AddFoodDTO addFoodDTO = new AddFoodDTO();
        addFoodDTO.setName("New Pizza");
        addFoodDTO.setPrice(10.00);
        MultipartFile image = mock(MultipartFile.class);

        when(utils.productWithThisNameExist(addFoodDTO.getName())).thenReturn(false);
        when(modelMapper.map(addFoodDTO, FoodEntity.class)).thenReturn(new FoodEntity());
        when(cloudinaryService.uploadImage(image, addFoodDTO.getName())).thenReturn("cloudImageURL");

        foodService.addFood(addFoodDTO, image);

        verify(cloudinaryService, times(1)).uploadImage(image, addFoodDTO.getName());
        verify(productRepository, times(1)).save(any());
    }

    @Test
    void getAllFoodsByType_Success() {

        FoodEntity foodEntity1 = new FoodEntity();
        foodEntity1.setId(1L);
        foodEntity1.setName("Margherita");
        foodEntity1.setFoodTypeEnum(FoodTypeEnum.PIZZA);

        FoodEntity foodEntity2 = new FoodEntity();
        foodEntity1.setId(2L);
        foodEntity1.setName("Prosciutto Crudo");
        foodEntity1.setFoodTypeEnum(FoodTypeEnum.PIZZA);

        List<FoodEntity> foodEntityList = List.of(foodEntity1, foodEntity2);
        Page<FoodEntity> foodEntityPage = new PageImpl<>(foodEntityList);
        PageRequest page = PageRequest.of(0, 6);

        when(productRepository.findAllByProductTypePageable(FoodTypeEnum.PIZZA, page)).thenReturn(foodEntityPage);

        FoodDTO foodDTO1 = new FoodDTO();
        FoodDTO foodDTO2 = new FoodDTO();

        List<FoodDTO> foodDTOList = List.of(foodDTO1, foodDTO2);
        when(modelMapper.map(any(), eq(FoodDTO.class))).thenReturn(foodDTOList.get(0), foodDTOList.get(1));

        Page<FoodDTO> resultPage = foodService.getAllFoodsByType("PIZZA", 0);

        assertEquals(foodEntityList.size(), resultPage.getContent().size());
        assertEquals(resultPage.getContent().get(0), foodDTO1);
        assertEquals(resultPage.getContent().get(1), foodDTO2);
    }

    @Test
    public void deleteFoodOrBeverage_ProductNotFound_Throw_ProductNotFoundException() {
        FoodEntity foodEntity = new FoodEntity();
        foodEntity.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> foodService.deleteFoodOrBeverage(1L));
        verify(productRepository, never()).deleteById(any());
        verify(cloudinaryService, never()).deleteProductImageFromCloudinary(any());
    }

    @Test
    public void deleteFoodOrBeverage_Success() {
        FoodEntity foodEntity = new FoodEntity();
        foodEntity.setId(1L);
        foodEntity.setImageURL("foodImageURL");

        when(productRepository.findById(1L)).thenReturn(Optional.of(foodEntity));

        foodService.deleteFoodOrBeverage(1L);

        verify(productRepository, times(1)).deleteById(foodEntity.getId());
        verify(cloudinaryService, times(1)).deleteProductImageFromCloudinary(foodEntity.getImageURL());
    }

    @Test
    void updateFood_WithoutProductImageProvided_Success() {
        Long productId = 1L;
        UpdateFoodDTO updateFoodDTO = new UpdateFoodDTO();
        updateFoodDTO.setName("Updated Pizza");

        FoodEntity existingFood = new FoodEntity();
        existingFood.setId(productId);
        existingFood.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        existingFood.setProductType(ProductTypeEnum.FOOD);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingFood));
        when(utils.productWithThisNameExist(updateFoodDTO.getName())).thenReturn(false);

        foodService.updateFood(productId, updateFoodDTO, null);

        verify(productRepository, times(1)).save(existingFood);
        verify(cloudinaryService, never()).uploadImage(any(), eq(updateFoodDTO.getName()));
    }

    @Test
    void updateFood_ProductImageProvided_Success() {
        Long productId = 1L;
        UpdateFoodDTO updateFoodDTO = new UpdateFoodDTO();
        updateFoodDTO.setName("Updated Pizza");

        FoodEntity existingFood = new FoodEntity();
        existingFood.setId(productId);
        existingFood.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        existingFood.setProductType(ProductTypeEnum.FOOD);

        MultipartFile image = mock(MultipartFile.class);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingFood));
        when(utils.productWithThisNameExist(updateFoodDTO.getName())).thenReturn(false);

        foodService.updateFood(productId, updateFoodDTO, image);

        verify(cloudinaryService, times(1)).validateImageFile(image);
        verify(cloudinaryService, times(1)).uploadImage(image, updateFoodDTO.getName());
        verify(productRepository, times(1)).save(existingFood);
    }

    @Test
    public void updateFood_ProductNameAssigned_Throw_IllegalArgumentException() {
        Long productId = 1L;
        UpdateFoodDTO dto = new UpdateFoodDTO();
        dto.setName("Some assigned name");

        when(utils.productWithThisNameExist(dto.getName())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> foodService.updateFood(productId, dto, any()));
    }

    @Test
    public void updateFood_InvalidFoodType_Throw_InvalidProductTypeException() {
        BeverageEntity beverage = new BeverageEntity();
        beverage.setProductType(ProductTypeEnum.BEVERAGE);
        beverage.setId(1L);

        Long productId = 1L;
        UpdateFoodDTO dto = new UpdateFoodDTO();

        when(productRepository.findById(productId)).thenReturn(Optional.of(beverage));
        assertThrows(InvalidProductTypeException.class, () -> foodService.updateFood(productId, dto, any()));
    }
}