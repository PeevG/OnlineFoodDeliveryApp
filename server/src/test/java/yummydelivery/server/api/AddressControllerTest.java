package yummydelivery.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import yummydelivery.server.config.SecurityConfig;
import yummydelivery.server.dto.AddressDTO;
import yummydelivery.server.dto.view.AddressView;
import yummydelivery.server.exceptions.AddressNotFoundException;
import yummydelivery.server.security.CustomUserDetailsService;
import yummydelivery.server.security.JwtAuthenticationEntryPoint;
import yummydelivery.server.security.JwtTokenProvider;
import yummydelivery.server.service.AddressService;
import yummydelivery.server.service.UserService;
import yummydelivery.server.utils.CommonUtils;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc()
@Import(SecurityConfig.class)
class AddressControllerTest {

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
    private AddressService addressService;
    @MockBean
    private UserService userService;
    @MockBean
    private CommonUtils utils;

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    public void getUserAddresses_ShouldReturnAddresses_With200() throws Exception {
        AddressView addressView = new AddressView();
        int page = 1;

        addressView.setId(13L);
        addressView.setCity("Sofia");
        addressView.setStreetName("Luiza");
        addressView.setStreetNumber("11");
        addressView.setPhoneNumber("094412341");

        Page<AddressView> addressPage = new PageImpl<>(Collections.singletonList(addressView));
        when(addressService.getUserAddresses(page)).thenReturn(addressPage);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(API_BASE + "/addresses")
                        .param("page", String.valueOf(page))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.body").exists())
                .andExpect(jsonPath("$.body.content").isArray())
                .andExpect(jsonPath("$.body.content[0]").exists())
                .andExpect(jsonPath("$.body.content.length()").value(1));

        verify(addressService, times(1)).getUserAddresses(page);
    }

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    public void addNewAddress_ValidAddress_ShouldReturnCreated201() throws Exception {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreetName("Some Street");
        addressDTO.setCity("Sofia");
        addressDTO.setPhoneNumber("359854654563");
        addressDTO.setStreetNumber("41");

        AddressDTO createdAddress = new AddressDTO();
        createdAddress.setStreetName(addressDTO.getStreetName());
        createdAddress.setCity(addressDTO.getCity());
        createdAddress.setStreetNumber(addressDTO.getStreetNumber());
        createdAddress.setPhoneNumber(addressDTO.getPhoneNumber());

        when(addressService.addNewAddress(addressDTO)).thenReturn(createdAddress);

        mockMvc.perform(post("/api/v1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((objectMapper.writeValueAsString(addressDTO))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.body.streetName").value(createdAddress.getStreetName()))
                .andExpect(jsonPath("$.body.city").value(createdAddress.getCity()))
                .andExpect(jsonPath("$.body.phoneNumber").value(createdAddress.getPhoneNumber()))
                .andExpect(jsonPath("$.body.streetNumber").value(createdAddress.getStreetNumber()));

        verify(addressService, times(1)).addNewAddress(addressDTO);
    }

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    public void addNewAddress_InvalidAddress_ShouldReturnCreated400_IfDTOValidationFails() throws Exception {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreetName("Some Street");

        mockMvc.perform(post("/api/v1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content((objectMapper.writeValueAsString(addressDTO))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(addressService, never()).addNewAddress(addressDTO);
    }

    @Test
    @WithMockUser(username = "currentUser", roles = "CUSTOMER")
    public void updateAddressById_ShouldSucceedWith200() throws Exception {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreetName("Some Street");
        addressDTO.setCity("Sofia");
        addressDTO.setPhoneNumber("359854654563");
        addressDTO.setStreetNumber("41");

        Long addressId = 1L;

        AddressDTO updatedAddressDTO = new AddressDTO();
        updatedAddressDTO.setStreetName(addressDTO.getStreetName());
        updatedAddressDTO.setCity(addressDTO.getCity());
        updatedAddressDTO.setPhoneNumber(addressDTO.getPhoneNumber());
        updatedAddressDTO.setStreetNumber(addressDTO.getStreetNumber());

        when(addressService.updateAddress(addressDTO, addressId)).thenReturn(updatedAddressDTO);

        mockMvc.perform(put("/api/v1/addresses/{addressId}", addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Address with id " + addressId + " is updated"))
                .andExpect(jsonPath("$.body.streetName").value(updatedAddressDTO.getStreetName()))
                .andExpect(jsonPath("$.body.city").value(updatedAddressDTO.getCity()))
                .andExpect(jsonPath("$.body.streetNumber").value(updatedAddressDTO.getStreetNumber()))
                .andExpect(jsonPath("$.body.phoneNumber").value(updatedAddressDTO.getPhoneNumber()));

        verify(addressService, times(1)).updateAddress(addressDTO, addressId);
    }

    @Test
    @WithMockUser(username = "currentUser", roles = "CUSTOMER")
    public void updateAddressById_ShouldFail_With400_IfDTOValidationFails() throws Exception {
        AddressDTO invalidAddressDTO = new AddressDTO();

        Long addressId = 1L;
        mockMvc.perform(put("/api/v1/addresses/{addressId}", addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAddressDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(addressService, never()).updateAddress(invalidAddressDTO, addressId);
    }

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    public void deleteAddressById_ShouldSucceed_WhenAddressDeleted() throws Exception {
        Long addressId = 1L;

        doNothing().when(addressService).deleteAddress(addressId);

        mockMvc.perform(delete(API_BASE + "/addresses/{addressId}", addressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Address with id " + addressId + " successfully deleted"));

        verify(addressService, times(1)).deleteAddress(addressId);
    }

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    public void deleteAddressById_ShouldReturnNotFound_WhenAddressNotFound() throws Exception {
        Long addressId = 1L;

        doThrow(new AddressNotFoundException(HttpStatus.NOT_FOUND, "Address with id " + addressId + " not found"))
                .when(addressService).deleteAddress(addressId);

        mockMvc.perform(delete(API_BASE + "/addresses/{addressId}", addressId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Address with id " + addressId + " not found"));

        verify(addressService, times(1)).deleteAddress(addressId);
    }
}