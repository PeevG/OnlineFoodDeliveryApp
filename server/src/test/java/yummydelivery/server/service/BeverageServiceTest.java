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
import yummydelivery.server.dto.BeverageDTO.BeverageDTO;
import yummydelivery.server.dto.view.BeverageView;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.BeverageNotFoundException;
import yummydelivery.server.exceptions.InvalidProductTypeException;
import yummydelivery.server.exceptions.ProductNotFoundException;
import yummydelivery.server.model.BeverageEntity;
import yummydelivery.server.model.FoodEntity;
import yummydelivery.server.model.Product;
import yummydelivery.server.repository.ProductRepository;
import yummydelivery.server.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BeverageServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private CommonUtils utils;

    @InjectMocks
    private BeverageService beverageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getBeverage_BeverageNotFound_ExceptionThrown() {
        Long productId = 10L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> beverageService.getBeverageById(productId));
    }

    @Test
    public void getBeverage_WrongProductType_ExceptionThrown() {
        Long productId = 10L;
        Product beverage = new FoodEntity();
        beverage.setId(10L);
        beverage.setProductType(ProductTypeEnum.FOOD);

        when(productRepository.findById(productId)).thenReturn(Optional.of(beverage));

        assertThrows(InvalidProductTypeException.class, () -> beverageService.getBeverageById(productId));
    }

    @Test
    public void getBeverage_Success() {
        Long productId = 10L;

        BeverageEntity beverage = new BeverageEntity();
        beverage.setName("Vodka");
        beverage.setPrice(5.00);
        beverage.setImageURL("someURL");
        beverage.setId(10L);
        beverage.setProductType(ProductTypeEnum.BEVERAGE);
        beverage.setMilliliters(50);

        BeverageView expectedBeverageView = new BeverageView();
        expectedBeverageView.setId(beverage.getId());
        expectedBeverageView.setName(beverage.getName());
        expectedBeverageView.setPrice(beverage.getPrice());
        expectedBeverageView.setImageURL(beverage.getImageURL());
        expectedBeverageView.setMilliliters(beverage.getMilliliters());


        when(productRepository.findById(productId)).thenReturn(Optional.of(beverage));
        when(modelMapper.map(beverage, BeverageView.class)).thenReturn(expectedBeverageView);

        BeverageView beverageView = beverageService.getBeverageById(productId);

        assertEquals(beverageView.getId(), beverage.getId());
        assertEquals(beverageView.getPrice(), beverage.getPrice());
        assertEquals(beverageView.getImageURL(), beverage.getImageURL());
        assertEquals(beverageView.getMilliliters(), beverage.getMilliliters());
    }

    @Test
    public void addBeverage_NameAlreadyOccupied_ExceptionThrown() {
        BeverageDTO dto = new BeverageDTO();
        dto.setName("newBev");

        Product product = new BeverageEntity();
        product.setName("newBev");

        when(utils.productWithThisNameExist(dto.getName())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> beverageService.addBeverage(dto, any()));
    }

    @Test
    public void addBeverage_NoImageProvided_UseDefaultImage_Success() {
        BeverageDTO dto = new BeverageDTO();
        dto.setName("newBev");
        dto.setMilliliters(100);
        dto.setPrice(3.00);

        BeverageEntity beverage = new BeverageEntity();
        beverage.setName(dto.getName());
        beverage.setPrice(dto.getPrice());
        beverage.setImageURL("defaultProductImageURL");
        beverage.setId(10L);
        beverage.setProductType(ProductTypeEnum.BEVERAGE);
        beverage.setMilliliters(50);

        when(utils.productWithThisNameExist(dto.getName())).thenReturn(false);
        when(modelMapper.map(dto, BeverageEntity.class)).thenReturn(beverage);

        beverageService.addBeverage(dto, null);

        verify(productRepository, times(1)).save(beverage);
    }

    @Test
    public void addBeverage_ImageIsProvided_Success() {
        BeverageDTO dto = new BeverageDTO();
        dto.setName("newBev");
        dto.setMilliliters(100);
        dto.setPrice(3.00);

        String cloudinaryURL = "CloudinaryURL";

        BeverageEntity beverage = new BeverageEntity();
        beverage.setName(dto.getName());
        beverage.setPrice(dto.getPrice());
        beverage.setImageURL(cloudinaryURL);
        beverage.setId(10L);
        beverage.setProductType(ProductTypeEnum.BEVERAGE);
        beverage.setMilliliters(50);

        MultipartFile image = mock();


        when(utils.productWithThisNameExist(dto.getName())).thenReturn(false);
        when(modelMapper.map(dto, BeverageEntity.class)).thenReturn(beverage);
        when(cloudinaryService.uploadImage(image, "imageFileName")).thenReturn(cloudinaryURL);

        beverageService.addBeverage(dto, image);

        verify(productRepository, times(1)).save(beverage);
        verify(cloudinaryService, times(1)).validateImageFile(image);
        verify(cloudinaryService, times(1)).uploadImage(image, dto.getName());
    }

    @Test
    public void updateBeverage_NameAlreadyOccupied_ExceptionThrown() {
        Long productId = 10L;

        BeverageDTO dto = new BeverageDTO();
        dto.setName("newBev");

        Product product = new BeverageEntity();
        product.setId(productId);
        product.setName("newBev");

        when(utils.productWithThisNameExist(dto.getName())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> beverageService.updateBeverage(productId, dto, any()));
    }

    @Test
    public void updateBeverage_BeverageNotFound_ExceptionThrown() {
        Long productId = 10L;
        BeverageDTO dto = new BeverageDTO();
        dto.setName("newBev");

        when(utils.productWithThisNameExist(dto.getName())).thenReturn(false);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(BeverageNotFoundException.class, () -> beverageService.updateBeverage(productId, dto, any()));
    }

    @Test
    public void updateBeverage_InvalidProductType_ExceptionThrown() {
        Long productId = 10L;
        BeverageDTO dto = new BeverageDTO();
        dto.setName("newBev");

        Product product = new FoodEntity();
        product.setId(productId);
        product.setProductType(ProductTypeEnum.FOOD);
        product.setName("newBev");

        when(utils.productWithThisNameExist(dto.getName())).thenReturn(false);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(InvalidProductTypeException.class, () -> beverageService.updateBeverage(productId, dto, any()));
    }

    @Test
    public void updateBeverage_NoImageProvided_UseDefaultImage_Success() {
        Long productId = 10L;
        BeverageDTO dto = new BeverageDTO();
        dto.setPrice(10.00);
        dto.setMilliliters(50);
        dto.setName("newBev");

        BeverageEntity product = new BeverageEntity();
        product.setId(productId);
        product.setProductType(ProductTypeEnum.BEVERAGE);
        product.setName("newBev");
        product.setImageURL("SomeOldURL");
        product.setPrice(dto.getPrice());
        product.setMilliliters(50);

        when(utils.productWithThisNameExist(dto.getName())).thenReturn(false);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        beverageService.updateBeverage(productId, dto, null);

        assertEquals(dto.getName(), product.getName());
        assertEquals(dto.getMilliliters(), product.getMilliliters());
        assertEquals(dto.getPrice(), product.getPrice());
        verify(cloudinaryService, never()).validateImageFile(any());
        verify(cloudinaryService, never()).uploadImage(any(), any());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    public void updateBeverage_ImageIsProvided_UpdateImageURL_Success() {
        Long productId = 10L;
        BeverageDTO dto = new BeverageDTO();
        dto.setPrice(10.00);
        dto.setMilliliters(50);
        dto.setName("newBev");

        String cloudURL = "CloudUrl";
        BeverageEntity product = new BeverageEntity();
        product.setId(productId);
        product.setProductType(ProductTypeEnum.BEVERAGE);
        product.setName("newBev");
        product.setImageURL("blabla");
        product.setPrice(dto.getPrice());
        product.setMilliliters(50);


        MultipartFile imageMock = mock();

        when(utils.productWithThisNameExist(dto.getName())).thenReturn(false);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cloudinaryService.uploadImage(imageMock, dto.getName())).thenReturn(cloudURL);

        beverageService.updateBeverage(productId, dto, imageMock);

        assertEquals(dto.getName(), product.getName());
        assertEquals(dto.getMilliliters(), product.getMilliliters());
        assertEquals(dto.getPrice(), product.getPrice());
        assertEquals(product.getImageURL(), cloudURL);
        verify(cloudinaryService, times(1)).validateImageFile(imageMock);
        verify(cloudinaryService, times(1)).uploadImage(imageMock, dto.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    public void getAllBeverages() {
        BeverageEntity beverage1 = new BeverageEntity();
        beverage1.setId(1L);
        beverage1.setName("Beverage 1");

        BeverageEntity beverage2 = new BeverageEntity();
        beverage2.setId(2L);
        beverage2.setName("Beverage 2");

        List<BeverageEntity> beveragesList = new ArrayList<>();
        beveragesList.add(beverage1);
        beveragesList.add(beverage2);

        PageRequest pageRequest = PageRequest.of(0, 6);
        Page<BeverageEntity> beverageEntityPage = new PageImpl<>(beveragesList, pageRequest, beveragesList.size());

        when(productRepository.findAllBeveragesPageable(pageRequest)).thenReturn(beverageEntityPage);

        BeverageView beverageView1 = new BeverageView();
        beverageView1.setId(1L);
        beverageView1.setName("Beverage 1");

        BeverageView beverageView2 = new BeverageView();
        beverageView2.setId(2L);
        beverageView2.setName("Beverage 2");

        List<BeverageView> viewList = new ArrayList<>();
        viewList.add(beverageView1);
        viewList.add(beverageView2);

        when(modelMapper.map(any(BeverageEntity.class), eq(BeverageView.class)))
                .thenAnswer(invocation -> {
                    BeverageEntity beverageEntity = invocation.getArgument(0);
                    return new BeverageView(beverageEntity.getId(), beverageEntity.getName(), beverageEntity.getPrice(), beverageEntity.getImageURL(), beverageEntity.getMilliliters());
                });

        // Act
        Page<BeverageView> result = beverageService.getAllBeverages(0);

        assertEquals(beverageEntityPage.getTotalElements(), result.getTotalElements());
        assertEquals(beverageEntityPage.getTotalPages(), result.getTotalPages());
        assertEquals(beverageView1.getId(), result.getContent().get(0).getId());
        assertEquals(beverageView2.getId(), result.getContent().get(1).getId());
    }
}