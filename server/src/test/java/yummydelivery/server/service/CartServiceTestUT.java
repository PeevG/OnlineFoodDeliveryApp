package yummydelivery.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import yummydelivery.server.dto.ShoppingCartDTO;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.ProductNotFoundException;
import yummydelivery.server.exceptions.ShoppingCartException;
import yummydelivery.server.model.*;
import yummydelivery.server.repository.CartItemRepository;
import yummydelivery.server.repository.CartRepository;
import yummydelivery.server.repository.ProductRepository;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.AuthenticationFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTestUT {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void getUserCart_Success() {
        UserEntity currentUser = new UserEntity();
        currentUser.setEmail("current@email.com");

        ShoppingCartEntity shoppingCart = new ShoppingCartEntity();

        List<CartItem> cartItems = new ArrayList<>();

        CartItem newItem = new CartItem();

        Product food = new FoodEntity();
        food.setName("Pizza");
        food.setProductType(ProductTypeEnum.FOOD);
        food.setPrice(10.00);

        cartItems.add(newItem);
        shoppingCart.setCartItems(cartItems);
        currentUser.setCart(shoppingCart);

        when(userService.getCurrentUserByUsername()).thenReturn(currentUser);

        ShoppingCartDTO userCart = cartService.getUserCart();

        assertEquals(newItem, userCart.getItems().get(0));
        assertEquals(1, userCart.getItems().size());
        assertEquals(shoppingCart.getCartPrice(), userCart.getCartPrice());
    }

    @Test
    public void removeCartItem_Success() {
        UserEntity currentUser = new UserEntity();
        currentUser.setEmail("current@email.com");

        ShoppingCartEntity shoppingCart = new ShoppingCartEntity();

        List<CartItem> cartItems = new ArrayList<>();

        CartItem cartItem = new CartItem();
        cartItem.setId(10L);

        Product food = new FoodEntity();
        food.setName("Pizza");
        food.setProductType(ProductTypeEnum.FOOD);
        food.setPrice(10.00);

        cartItems.add(cartItem);
        shoppingCart.setCartItems(cartItems);
        currentUser.setCart(shoppingCart);

        Authentication authentication = mock();

        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUser.getEmail());
        when(userService.getCurrentUserByUsername()).thenReturn(currentUser);
        when(cartItemRepository.findById(10L)).thenReturn(Optional.of(cartItem));

        cartService.removeItemFromCart(10L);

        assertEquals(0, cartItems.size());
        verify(userRepository, times(1)).save(currentUser);
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    public void removeCartItem_ItemNotFound_ExceptionThrown() {
        UserEntity currentUser = new UserEntity();
        currentUser.setEmail("current@email.com");

        when(userService.getCurrentUserByUsername()).thenReturn(currentUser);

        assertThrows(ShoppingCartException.class, () -> cartService.removeItemFromCart(50L));
        verify(cartItemRepository, never()).delete(any());
        verify(userRepository, never()).save(currentUser);
    }

    @Test
    public void addItemToShoppingCart_ProductNotInShoppingCart_Success() {
        UserEntity currentUser = new UserEntity();
        currentUser.setEmail("current@email.com");
        Long productId = 13L;

        Product food = new FoodEntity();
        food.setId(13L);
        food.setName("Pizza");
        food.setProductType(ProductTypeEnum.FOOD);
        food.setPrice(10.00);

        CartItem expectedCartItem = new CartItem();
        expectedCartItem.setQuantity(1);
        expectedCartItem.setProduct(food);
        expectedCartItem.setPrice(10.00);
        Authentication authentication = mock();

        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUser.getEmail());
        when(productRepository.findById(productId)).thenReturn(Optional.of(food));
        when(userService.getCurrentUserByUsername()).thenReturn(currentUser);

        cartService.addItemToShoppingCart(13L);

        verify(cartItemRepository, times(1)).save(expectedCartItem);
        verify(cartRepository, times(1)).save(any(ShoppingCartEntity.class));

        assertEquals(1, currentUser.getCart().getCartItems().size());
        assertEquals(13L, currentUser.getCart().getCartItems().get(0).getProduct().getId());
        assertEquals(1, currentUser.getCart().getCartItems().get(0).getQuantity());
        assertEquals(10, currentUser.getCart().getCartPrice());
        assertEquals(expectedCartItem, currentUser.getCart().getCartItems().get(0));
    }

    @Test
    public void addItemToShoppingCart_ProductExistInCart_Success() {
        UserEntity currentUser = new UserEntity();
        currentUser.setEmail("current@email.com");
        Long productId = 13L;

        Product food = new FoodEntity();
        food.setId(13L);
        food.setName("Pizza");
        food.setProductType(ProductTypeEnum.FOOD);
        food.setPrice(10.00);

        CartItem cartItem = new CartItem();
        cartItem.setId(8L);
        cartItem.setQuantity(1);
        cartItem.setProduct(food);
        cartItem.setPrice(10.00);

        CartItem expectedItem = new CartItem();
        expectedItem.setId(8L);
        expectedItem.setQuantity(2);
        expectedItem.setProduct(food);
        expectedItem.setPrice(20.00);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        ShoppingCartEntity shoppingCart = new ShoppingCartEntity();
        shoppingCart.setCartItems(cartItems);

        currentUser.setCart(shoppingCart);
        Authentication authentication = mock();

        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUser.getEmail());
        when(productRepository.findById(productId)).thenReturn(Optional.of(food));
        when(userService.getCurrentUserByUsername()).thenReturn(currentUser);

        cartService.addItemToShoppingCart(13L);

        verify(cartItemRepository, times(1)).save(expectedItem);
        verify(cartRepository, times(1)).save(shoppingCart);

        assertEquals(1, currentUser.getCart().getCartItems().size());
        assertEquals(13L, currentUser.getCart().getCartItems().get(0).getProduct().getId());
        assertEquals(2, currentUser.getCart().getCartItems().get(0).getQuantity());
        assertEquals(20, currentUser.getCart().getCartPrice());
        assertEquals(expectedItem, currentUser.getCart().getCartItems().get(0));
    }

    @Test
    public void addItemToShoppingCart_ProductNotFound_ExceptionThrown() {
        Long productId = 16L;

        UserEntity user = new UserEntity();
        user.setId(5L);
        Authentication authenticationMock = mock();


        when(authenticationFacade.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(true);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> cartService.addItemToShoppingCart(productId));
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartRepository, never()).save(any(ShoppingCartEntity.class));
    }
}