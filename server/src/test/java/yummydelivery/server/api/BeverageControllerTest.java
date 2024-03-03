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
import yummydelivery.server.dto.BeverageDTO.BeverageDTO;
import yummydelivery.server.dto.view.BeverageView;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.ProductNotFoundException;
import yummydelivery.server.model.BeverageEntity;
import yummydelivery.server.security.CustomUserDetailsService;
import yummydelivery.server.security.JwtAuthenticationEntryPoint;
import yummydelivery.server.security.JwtTokenProvider;
import yummydelivery.server.service.BeverageService;
import yummydelivery.server.service.FoodService;
import yummydelivery.server.utils.CommonUtils;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@WebMvcTest(BeverageController.class)
@AutoConfigureMockMvc()
@Import(SecurityConfig.class)
@Slf4j
class BeverageControllerTest {
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
    private BeverageService beverageService;
    @MockBean
    private FoodService foodService;
    @MockBean
    private CommonUtils commonUtils;

    @Test
    @WithAnonymousUser
    public void getBeverageById_ShouldSucceed_OK200() throws Exception {
        BeverageEntity beverageEntity = new BeverageEntity();
        beverageEntity.setId(15L);
        beverageEntity.setProductType(ProductTypeEnum.BEVERAGE);
        beverageEntity.setName("Coca Cola");
        beverageEntity.setMilliliters(250);
        beverageEntity.setPrice(4.50);
        beverageEntity.setImageURL("image");

        BeverageView expectedDTo = new BeverageView();
        expectedDTo.setId(beverageEntity.getId());
        expectedDTo.setName(beverageEntity.getName());
        expectedDTo.setMilliliters(beverageEntity.getMilliliters());
        expectedDTo.setPrice(beverageEntity.getPrice());
        expectedDTo.setImageURL(beverageEntity.getImageURL());

        when(beverageService.getBeverageById(15L)).thenReturn(expectedDTo);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE + "/beverages/{id}", 15L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.body.id").value(expectedDTo.getId()))
                .andExpect(jsonPath("$.body.name").value(expectedDTo.getName()))
                .andExpect(jsonPath("$.body.milliliters").value(expectedDTo.getMilliliters()))
                .andExpect(jsonPath("$.body.imageURL").value(expectedDTo.getImageURL()));

        verify(beverageService, times(1)).getBeverageById(15L);
    }

    @Test
    @WithAnonymousUser
    public void getBeverageById_ShouldFail_NotFound404() throws Exception {

        Long nonExistentFoodId = 1000L;
        when(beverageService.getBeverageById(nonExistentFoodId)).thenThrow(new ProductNotFoundException(HttpStatus.NOT_FOUND, "Product not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE + "/beverages/{id}", nonExistentFoodId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()));

        verify(beverageService, times(1)).getBeverageById(nonExistentFoodId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void addBeverage_ShouldSucceed_Created201() throws Exception {
        BeverageDTO beverageDTO = new BeverageDTO();
        beverageDTO.setName("Coca Cola");
        beverageDTO.setMilliliters(250);
        beverageDTO.setPrice(3.00);

        MockMultipartFile dtoMetaData = new MockMultipartFile("beverageDTO", "bev",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(beverageDTO).getBytes());

        MockMultipartFile imageFile = new MockMultipartFile(
                "productImage",
                "Coca Cola.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes());

        doNothing().when(beverageService).addBeverage(beverageDTO, imageFile);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/beverages")
                        .file(dtoMetaData)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()));

        verify(beverageService, times(1)).addBeverage(beverageDTO, imageFile);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void addBeverage_ShouldFail_Forbidden403() throws Exception {
        BeverageDTO beverageDTO = new BeverageDTO();
        beverageDTO.setName("Coca Cola");
        beverageDTO.setMilliliters(250);
        beverageDTO.setPrice(3.00);

        MockMultipartFile dtoMetaData = new MockMultipartFile("beverageDTO", "bev",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(beverageDTO).getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/beverages")
                        .file(dtoMetaData)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isForbidden());

        verify(beverageService, never()).addBeverage(beverageDTO, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void addBeverage_ShouldFail_BadRequest400() throws Exception {
        //Invalid DTO -> 'name' field missing
        BeverageDTO beverageDTO = new BeverageDTO();
        //beverageDTO.setName("Coca Cola");
        beverageDTO.setMilliliters(250);
        beverageDTO.setPrice(3.00);

        MockMultipartFile dtoMetaData = new MockMultipartFile("beverageDTO", "bev",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(beverageDTO).getBytes());

        MockMultipartFile imageFile = new MockMultipartFile(
                "productImage",
                "Coca Cola.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/beverages")
                        .file(dtoMetaData)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(beverageService, never()).addBeverage(beverageDTO, imageFile);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void deleteBeverageById_ShouldFail_Unauthorized403() throws Exception {
        Long idToDelete = 13L;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(API_BASE + "/beverages/{id}", idToDelete))
                .andExpect(status().isForbidden());
        verify(foodService, never()).deleteFoodOrBeverage(idToDelete);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteBeverageById_ShouldSucceedWith200() throws Exception {
        Long idToDelete = 13L;

        doNothing().when(foodService).deleteFoodOrBeverage(idToDelete);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(API_BASE + "/beverages/{id}", idToDelete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message")
                        .value("Beverage with id " + idToDelete + " is deleted successfully"));
        verify(foodService, times(1)).deleteFoodOrBeverage(idToDelete);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteBeverageById_ShouldFail_With404() throws Exception {
        Long nonExistentFoodId = 1000L;

        doThrow(new ProductNotFoundException(HttpStatus.NOT_FOUND, "Product with id " + nonExistentFoodId + " not found"))
                .when(foodService).deleteFoodOrBeverage(nonExistentFoodId);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(API_BASE + "/beverages/{id}", nonExistentFoodId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Product with id " + nonExistentFoodId + " not found"));


        verify(foodService, times(1)).deleteFoodOrBeverage(nonExistentFoodId);
    }

    @Test
    public void getAllBeverages_ShouldSuccess_With200() throws Exception {
        int page = 0;

        Page<BeverageView> beveragePage = createMockBeveragePage();
        when(beverageService.getAllBeverages(page)).thenReturn(beveragePage);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE + "/beverages")
                        .param("page", String.valueOf(page))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.body").exists())
                .andExpect(jsonPath("$.body.content").isArray())
                .andExpect(jsonPath("$.body.content[0].name").value(beveragePage.getContent().get(0).getName()))
                .andExpect(jsonPath("$.body.content.length()").value(beveragePage.getContent().size()));

        verify(beverageService, times(1)).getAllBeverages(page);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateBeverage_ShouldSucceed_OK200() throws Exception {
        Long beverageId = 1L;
        BeverageDTO beverageDTO = new BeverageDTO();
        beverageDTO.setName("Coca Cola");
        beverageDTO.setPrice(4.99);
        beverageDTO.setMilliliters(250);

        MockMultipartFile beverageDTOToFile = new MockMultipartFile("dto", "dto.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(beverageDTO));

        MockMultipartFile imageFile =
                new MockMultipartFile("productImage", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());

        doNothing().when(beverageService).updateBeverage(beverageId, beverageDTO, imageFile);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/beverages/{id}", beverageId)
                        .file(beverageDTOToFile)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Beverage with id " + beverageId + " is updated successfully"));

        verify(beverageService, times(1)).updateBeverage(beverageId, beverageDTO, imageFile);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateBeverage_ShouldFail_BadRequest400() throws Exception {
        Long beverageId = 1L;
        BeverageDTO beverageDTO = new BeverageDTO();

        MockMultipartFile beverageDTOToFile = new MockMultipartFile("dto", "dto.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(beverageDTO));

        MockMultipartFile imageFile =
                new MockMultipartFile("productImage", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(API_BASE + "/beverages/{id}", beverageId)
                        .file(beverageDTOToFile)
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(beverageService, never()).updateBeverage(beverageId, beverageDTO, imageFile);
    }

    private Page<BeverageView> createMockBeveragePage() {
        BeverageView view = new BeverageView();
        view.setId(10L);
        view.setPrice(8.99);
        view.setName("Orange Juice");
        view.setMilliliters(200);
        view.setImageURL(null);
        return new PageImpl<>(Collections.singletonList(view));
    }
}