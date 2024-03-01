package yummydelivery.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import yummydelivery.server.config.SecurityConfig;
import yummydelivery.server.dto.foodDTO.AddFoodDTO;
import yummydelivery.server.dto.foodDTO.FoodDTO;
import yummydelivery.server.enums.FoodTypeEnum;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.ProductNotFoundException;
import yummydelivery.server.model.FoodEntity;
import yummydelivery.server.security.CustomUserDetailsService;
import yummydelivery.server.security.JwtAuthenticationEntryPoint;
import yummydelivery.server.security.JwtTokenProvider;
import yummydelivery.server.service.FoodService;
import yummydelivery.server.utils.CommonUtils;

import static org.mockito.Mockito.*;
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
    public void getFoodById_ShouldSucceedWith200() throws Exception {
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.id").value(expectedDTo.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.name").value(expectedDTo.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body.grams").value(expectedDTo.getGrams()));

        verify(foodService, times(1)).getFoodById(15L);
    }

    @Test
    @WithAnonymousUser
    public void getFoodById_ShouldFailWith404() throws Exception {

        Long nonExistentFoodId = 1000L;
        when(foodService.getFoodById(nonExistentFoodId)).thenThrow(new ProductNotFoundException(HttpStatus.NOT_FOUND, "Product not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE + "/foods/{id}", nonExistentFoodId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()));

        verify(foodService, times(1)).getFoodById(nonExistentFoodId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addFood_ShouldSucceedWith201() throws Exception {
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
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(HttpStatus.CREATED.value()));

        verify(foodService, times(1)).addFood(any(), any());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void addFood_ShouldFailWith403() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/foods")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        verify(foodService, never()).addFood(any(), any());
    }
}