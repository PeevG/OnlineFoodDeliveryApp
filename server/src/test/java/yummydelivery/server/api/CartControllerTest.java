package yummydelivery.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import yummydelivery.server.config.SecurityConfig;
import yummydelivery.server.dto.ShoppingCartDTO;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.ProductNotFoundException;
import yummydelivery.server.exceptions.ShoppingCartException;
import yummydelivery.server.model.BeverageEntity;
import yummydelivery.server.model.CartItem;
import yummydelivery.server.security.CustomUserDetailsService;
import yummydelivery.server.security.JwtAuthenticationEntryPoint;
import yummydelivery.server.security.JwtTokenProvider;
import yummydelivery.server.service.CartService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc()
@Import(SecurityConfig.class)
class CartControllerTest {

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
    private CartService cartService;

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    public void getUserCart_ShouldReturnUserCart_With200() throws Exception {
        BeverageEntity beverageProduct = new BeverageEntity();
        beverageProduct.setImageURL(null);
        beverageProduct.setMilliliters(100);
        beverageProduct.setProductType(ProductTypeEnum.BEVERAGE);
        beverageProduct.setName("Uzo");
        beverageProduct.setPrice(10.10);
        beverageProduct.setId(12L);

        CartItem cartItem = new CartItem();
        cartItem.setId(15L);
        cartItem.setPrice(15.50);
        cartItem.setQuantity(1);
        cartItem.setProduct(beverageProduct);

        List<CartItem> userCartItems = new ArrayList<>();
        userCartItems.add(cartItem);

        ShoppingCartDTO userCart = new ShoppingCartDTO();
        userCart.setItems(userCartItems);

        when(cartService.getUserCart()).thenReturn(userCart);

        mockMvc.perform(get(API_BASE + "/shoppingCart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.body.items.length()").value(userCartItems.size()))
                .andExpect(jsonPath("$.body.items[0].price").value(cartItem.getPrice()))
                .andExpect(jsonPath("$.body.items[0].quantity").value(cartItem.getQuantity()));

        verify(cartService, times(1)).getUserCart();
    }

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    public void addItemToCart_ShouldAddItem_With200() throws Exception {
        Long productId = 1L;

        mockMvc.perform(post(API_BASE + "/menu/addToCart/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.body").value("Product with id " + productId + " is added to cart successfully"));

        verify(cartService, times(1)).addItemToShoppingCart(productId);
    }

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    public void addItemToCart_ShouldReturnNotFound_WhenProductNotFound() throws Exception {
        Long productId = 1L;

        doThrow(new ProductNotFoundException(HttpStatus.NOT_FOUND, "Product not found"))
                .when(cartService).addItemToShoppingCart(anyLong());

        mockMvc.perform(post(API_BASE + "/menu/addToCart/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Product not found"));

        verify(cartService, times(1)).addItemToShoppingCart(productId);
    }

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    public void removeItemFromCart_ShouldReturn404_WhenCartItemNotFound() throws Exception {
        Long cartItemId = 1L;

        doThrow(new ShoppingCartException(HttpStatus.NOT_FOUND, "Cart Item not found"))
                .when(cartService).removeItemFromCart(cartItemId);

        mockMvc.perform(delete(API_BASE + "/shoppingCart/{cartItemId}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("Cart Item not found"));

        verify(cartService, times(1)).removeItemFromCart(cartItemId);
    }

    @Test
    @WithMockUser(value = "currentUser", roles = "CUSTOMER")
    public void removeItemFromCart_ShouldRemoveItem_With200() throws Exception {
        Long cartItemId = 1L;

        mockMvc.perform(delete(API_BASE + "/shoppingCart/{cartItemId}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Cart item is removed successfully"));

        verify(cartService, times(1)).removeItemFromCart(cartItemId);
    }
}