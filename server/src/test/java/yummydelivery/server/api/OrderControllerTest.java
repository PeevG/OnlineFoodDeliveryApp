package yummydelivery.server.api;

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
import yummydelivery.server.dto.view.OrderView;
import yummydelivery.server.model.AddressEntity;
import yummydelivery.server.security.CustomUserDetailsService;
import yummydelivery.server.security.JwtAuthenticationEntryPoint;
import yummydelivery.server.security.JwtTokenProvider;
import yummydelivery.server.service.OrderService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private CustomUserDetailsService userDetailsService;
    @MockBean
    private OrderService orderService;

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    void createOrderForSelectedAddress_ShouldReturnOK() throws Exception {
        Long addressId = 4L;
        AddressEntity address = new AddressEntity();
        address.setId(addressId);
        address.setCity("Sofia");
        address.setStreetNumber("14");
        address.setPhoneNumber("35988855213");
        address.setStreetName("Qwerty");

        doNothing().when(orderService).createOrder(addressId);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(API_BASE + "/orders/{addressId}", addressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Order created successfully"));

        verify(orderService, times(1)).createOrder(addressId);
    }

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    public void testGetUserOrders_Successful() throws Exception {
        Page<OrderView> userOrders = createMockUserOrdersPage();
        when(orderService.getUserOrders(0)).thenReturn(userOrders);

        mockMvc.perform(get(API_BASE + "/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("User orders retrieved successfully"));

        verify(orderService, times(1)).getUserOrders(0);
    }

    private Page<OrderView> createMockUserOrdersPage() {
        OrderView order1 = new OrderView();
        order1.setId(1L);
        OrderView order2 = new OrderView();
        order2.setId(2L);
        List<OrderView> orderList = Arrays.asList(order1, order2);
        return new PageImpl<>(orderList);
    }
}