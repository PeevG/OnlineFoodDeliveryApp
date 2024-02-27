package yummydelivery.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BindingResult;
import yummydelivery.server.dto.UpdatePasswordDTO;
import yummydelivery.server.service.UserService;
import yummydelivery.server.utils.CommonUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@Slf4j
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private CommonUtils commonUtils;


    @WithMockUser(username = "someone@gmail.com", password = "hehehe")
    @Test
    public void changeUserPassword_ShouldSucceedWith200() throws Exception {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setOldPassword("somePassword");
        dto.setNewPassword("NewPassword");
        dto.setRepeatNewPassword("NewPassword");

        mockMvc.perform(MockMvcRequestBuilders
                        .put(API_BASE + "/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User password is changed successfully"));

        verify(userService, times(1)).updateUserPassword(any());
    }

    @WithMockUser(username = "someone@gmail.com", password = "hehehe")
    @Test
    public void changeUserPassword_ShouldFailWith400() throws Exception {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setOldPassword(null);
        dto.setNewPassword("dsfsdf");
        dto.setRepeatNewPassword("wwww");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(userService, never()).updateUserPassword(any());
    }

    @WithAnonymousUser
    @Test
    public void changeUserPassword_ShouldFailWith401() throws Exception {
        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setOldPassword("asdwqwe");
        dto.setNewPassword("dsfsdf");
        dto.setRepeatNewPassword("wwww");

        BindingResult bindingResult = mock();
        when(bindingResult.hasErrors()).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        verify(userService, never()).updateUserPassword(any());
    }
}