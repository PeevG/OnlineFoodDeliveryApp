package yummydelivery.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import yummydelivery.server.dto.JwtResponseDTO;
import yummydelivery.server.dto.SignInDTO;
import yummydelivery.server.dto.SignUpDTO;
import yummydelivery.server.service.AuthService;
import yummydelivery.server.utils.CommonUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Slf4j
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;
    @MockBean
    private CommonUtils utils;


    @Test
    public void signUp_ShouldSucceedWith201() throws Exception {
        SignUpDTO registerDTO = new SignUpDTO();
        registerDTO.setEmail("qwert@gmail.com");
        registerDTO.setCity("Rome");
        registerDTO.setPassword("qwerty");
        registerDTO.setFirstName("Mauricio");
        registerDTO.setLastName("DelPotro");
        registerDTO.setStreetNumber("12");
        registerDTO.setStreetName("Vinchenca");
        registerDTO.setPhoneNumber("882123456785");

        mockMvc.perform(MockMvcRequestBuilders
                        .post(API_BASE + "/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(registerDTO))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("Signed up successfully"));

        verify(authService, times(1)).signUpUser(any());
    }

    @Test
    public void signUp_ShouldFailWith400() throws Exception {
        SignUpDTO registerDTO = new SignUpDTO();
        registerDTO.setCity("Rome");
        registerDTO.setPassword("qwerty");
        registerDTO.setFirstName("Mauricio");
        registerDTO.setLastName("DelPotro");
        registerDTO.setStreetNumber("12");
        registerDTO.setStreetName("Vinchenca");
        registerDTO.setPhoneNumber("882123456785");


        mockMvc.perform(MockMvcRequestBuilders
                        .post(API_BASE + "/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(registerDTO))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(authService, never()).signUpUser(any());
    }

    @Test
    public void signIp_ShouldSucceedWith200() throws Exception {
        SignInDTO loginDTO = new SignInDTO();
        loginDTO.setEmail("qwert@gmail.com");
        loginDTO.setPassword("ytrewq");

        String mockToken = "mockToken";
        when(authService.signInUser(any(SignInDTO.class))).thenReturn(mockToken);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("User logged successfully"))
                .andExpect(jsonPath("$.body.accessToken").value(mockToken));

        verify(authService, times(1)).signInUser(any());
    }
}