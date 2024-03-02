package yummydelivery.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import yummydelivery.server.config.SecurityConfig;
import yummydelivery.server.dto.foodDTO.AddFoodDTO;
import yummydelivery.server.dto.foodDTO.FoodDTO;
import yummydelivery.server.dto.foodDTO.UpdateFoodDTO;
import yummydelivery.server.enums.FoodTypeEnum;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.ProductNotFoundException;
import yummydelivery.server.model.FoodEntity;
import yummydelivery.server.security.CustomUserDetailsService;
import yummydelivery.server.security.JwtAuthenticationEntryPoint;
import yummydelivery.server.security.JwtTokenProvider;
import yummydelivery.server.service.FoodService;
import yummydelivery.server.utils.CommonUtils;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@WebMvcTest(FoodController.class)
@AutoConfigureMockMvc()
@Import(SecurityConfig.class)
@Slf4j
class FoodControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    @MockBean
    private FoodService foodService;
    @MockBean
    private CommonUtils commonUtils;

    @Test
    @WithAnonymousUser
    public void getFoodById_ShouldSucceed_OK200() throws Exception {
        FoodEntity foodEntity = new FoodEntity();
        foodEntity.setId(15L);
        foodEntity.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        foodEntity.setProductType(ProductTypeEnum.FOOD);
        foodEntity.setName("Margaritta");
        foodEntity.setGrams(500);
        foodEntity.setPrice(10.10);

        FoodDTO expectedDTo = new FoodDTO();
        expectedDTo.setId(15L);
        expectedDTo.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        expectedDTo.setName(foodEntity.getName());
        expectedDTo.setId(foodEntity.getId());
        expectedDTo.setGrams(foodEntity.getGrams());
        expectedDTo.setPrice(10.10);

        when(foodService.getFoodById(15L)).thenReturn(expectedDTo);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE + "/foods/{id}", 15L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.body.id").value(expectedDTo.getId()))
                .andExpect(jsonPath("$.body.name").value(expectedDTo.getName()))
                .andExpect(jsonPath("$.body.grams").value(expectedDTo.getGrams()));

        verify(foodService, times(1)).getFoodById(15L);
    }

    @Test
    @WithAnonymousUser
    public void getFoodById_ShouldFail_NotFound404() throws Exception {

        Long nonExistentFoodId = 1000L;
        when(foodService.getFoodById(nonExistentFoodId)).thenThrow(new ProductNotFoundException(HttpStatus.NOT_FOUND, "Product not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE + "/foods/{id}", nonExistentFoodId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()));

        verify(foodService, times(1)).getFoodById(nonExistentFoodId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addFood_ShouldSucceed_Created201() throws Exception {
        AddFoodDTO addFoodDTO = new AddFoodDTO();
        addFoodDTO.setName("Roma");
        addFoodDTO.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        addFoodDTO.setGrams(550);
        addFoodDTO.setPrice(15.00);

        MockMultipartFile dtoMetaData = new MockMultipartFile("productInfo", "product",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(addFoodDTO).getBytes());

        MockMultipartFile imageFile = new MockMultipartFile(
                "productImage",
                "roma.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes());

        doNothing().when(foodService).addFood(addFoodDTO, imageFile);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/foods")
                        .file(dtoMetaData)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()));

        verify(foodService, times(1)).addFood(any(), any());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void addFood_ShouldFail_Forbidden403() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/foods")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isForbidden());

        verify(foodService, never()).addFood(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void addFood_ShouldFail_BadRequest400() throws Exception {
        //Invalid DTO -> 'name' field missing
        AddFoodDTO addFoodDTO = new AddFoodDTO();
        addFoodDTO.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        addFoodDTO.setGrams(550);
        addFoodDTO.setPrice(15.00);


        MockMultipartFile dtoMetaData = new MockMultipartFile("productInfo", "product",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(addFoodDTO).getBytes());

        MockMultipartFile imageFile = new MockMultipartFile(
                "productImage",
                "roma.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/foods")
                        .file(dtoMetaData)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(foodService, never()).addFood(any(), any());
    }


    @Test
    public void getFoodByType_ShouldSuccess_Ok200() throws Exception {
        String requestedFoodType = "pizza";

        Page<FoodDTO> mockPage = createMockFoodPage();
        when(foodService.getAllFoodsByType(requestedFoodType, 0)).thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE + "/foods")
                        .param("foodType", requestedFoodType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.body").exists())
                .andExpect(jsonPath("$.body.content").isArray())
                .andExpect(jsonPath("$.body.content[0].name").value(mockPage.getContent().get(0).getName()))
                .andExpect(jsonPath("$.body.content.length()").value(mockPage.getContent().size()));

        verify(foodService, times(1)).getAllFoodsByType(requestedFoodType, 0);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void deleteFoodById_ShouldFail_Unauthorized403() throws Exception {
        Long idToDelete = 13L;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(API_BASE + "/foods/{id}", idToDelete))
                .andExpect(status().isForbidden());
        verify(foodService, never()).deleteFoodOrBeverage(idToDelete);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteFoodById_ShouldSucceedWith200() throws Exception {
        Long idToDelete = 13L;

        doNothing().when(foodService).deleteFoodOrBeverage(idToDelete);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(API_BASE + "/foods/{id}", idToDelete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message")
                        .value("Food with id " + idToDelete + " is deleted successfully"));
        verify(foodService, times(1)).deleteFoodOrBeverage(idToDelete);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteFoodById_ShouldFail_With404() throws Exception {
        Long nonExistentFoodId = 1000L;

        doThrow(new ProductNotFoundException(HttpStatus.NOT_FOUND, "Product with id " + nonExistentFoodId + " not found"))
                .when(foodService).deleteFoodOrBeverage(nonExistentFoodId);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(API_BASE + "/foods/{id}", nonExistentFoodId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Product with id " + nonExistentFoodId + " not found"));


        verify(foodService, times(1)).deleteFoodOrBeverage(nonExistentFoodId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateFood_ShouldSucceed_OK200() throws Exception {
        Long foodId = 1L;
        UpdateFoodDTO updateFoodDTO = new UpdateFoodDTO();
        updateFoodDTO.setName("Roma");
        updateFoodDTO.setPrice(15.99);
        updateFoodDTO.setGrams(650);
        updateFoodDTO.setFoodTypeEnum(FoodTypeEnum.PIZZA);

        MockMultipartFile updateFoodDTOFile = new MockMultipartFile("updateFoodDTO", "updateFoodDTO.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(updateFoodDTO));

        MockMultipartFile imageFile =
                new MockMultipartFile("productImage", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());

        doNothing().when(foodService).updateFood(foodId, updateFoodDTO, imageFile);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/foods/{id}", foodId)
                        .file(updateFoodDTOFile)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Food with id " + foodId + " is updated successfully"));

        verify(foodService, times(1)).updateFood(foodId, updateFoodDTO, imageFile);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateFood_ShouldFail_BadRequest400() throws Exception {
        Long foodId = 1L;
        UpdateFoodDTO updateFoodDTO = new UpdateFoodDTO();

        MockMultipartFile updateFoodDTOFile = new MockMultipartFile("updateFoodDTO", "updateFoodDTO.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(updateFoodDTO));

        MockMultipartFile imageFile =
                new MockMultipartFile("productImage", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/foods/{id}", foodId)
                        .file(updateFoodDTOFile)
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(foodService, never()).updateFood(foodId, updateFoodDTO, imageFile);
    }

    private Page<FoodDTO> createMockFoodPage() {
        FoodDTO foodDTO = new FoodDTO();
        foodDTO.setId(1L);
        foodDTO.setName("Margherita");
        foodDTO.setPrice(10.99);
        foodDTO.setFoodTypeEnum(FoodTypeEnum.PIZZA);
        foodDTO.setGrams(600);

        return new PageImpl<>(Collections.singletonList(foodDTO));
    }
}