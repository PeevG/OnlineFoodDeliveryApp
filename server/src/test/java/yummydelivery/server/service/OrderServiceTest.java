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
import org.springframework.security.core.Authentication;
import yummydelivery.server.dto.view.OrderView;
import yummydelivery.server.enums.ProductTypeEnum;
import yummydelivery.server.exceptions.AddressNotFoundException;
import yummydelivery.server.model.*;
import yummydelivery.server.repository.AddressRepository;
import yummydelivery.server.repository.OrderRepository;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.AuthenticationFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getUserOrders_Success() {
        UserEntity user = new UserEntity();
        user.setEmail("user@email.com");
        user.setId(3L);

        Product orangeJuice = new BeverageEntity();
        orangeJuice.setId(2L);
        orangeJuice.setProductType(ProductTypeEnum.BEVERAGE);
        orangeJuice.setName("Orange juice");
        orangeJuice.setPrice(5.00);

        Product water = new BeverageEntity();
        water.setId(6L);
        water.setProductType(ProductTypeEnum.BEVERAGE);
        water.setName("Water");
        water.setPrice(3.00);

        CartItem orangeJuiceCartItem = new CartItem();
        orangeJuiceCartItem.setProduct(orangeJuice);
        orangeJuiceCartItem.setQuantity(1);
        orangeJuiceCartItem.setPrice(5.00);
        orangeJuiceCartItem.setId(7L);

        CartItem waterCartItem = new CartItem();
        waterCartItem.setProduct(water);
        waterCartItem.setQuantity(1);
        waterCartItem.setPrice(3.00);
        waterCartItem.setId(6L);

        List<CartItem> firstOrderProducts = new ArrayList<>();
        firstOrderProducts.add(orangeJuiceCartItem);

        List<CartItem> secondOrderProducts = new ArrayList<>();
        secondOrderProducts.add(waterCartItem);

        OrderEntity firstOrder = new OrderEntity();
        firstOrder.setOrderedProducts(firstOrderProducts);

        OrderEntity secondOrder = new OrderEntity();
        secondOrder.setOrderedProducts(secondOrderProducts);

        List<OrderEntity> orders = new ArrayList<>();
        orders.add(firstOrder);
        orders.add(secondOrder);

        user.setOrders(orders);

        Page<OrderEntity> ordersPage = new PageImpl<>(orders);
        PageRequest pageRequest = PageRequest.of(0, 6);
        Authentication authenticationMock = mock();

        when(authenticationFacade.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getName()).thenReturn("user@email.com");
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
        when(orderRepository.findAllByUserId(user.getId(), pageRequest)).thenReturn(ordersPage);

        Page<OrderView> orderViews = orderService.getUserOrders(1);

        assertNotNull(orderViews);
        assertEquals(1, orderViews.getTotalPages());
        assertEquals(2, orderViews.getTotalElements());
    }


    @Test
    public void createOrder_AddressNotFound_ExceptionThrown() {
        Long addressId = 6L;
        Authentication mockAuth = mock();

        when(authenticationFacade.getAuthentication()).thenReturn(mockAuth);
        when(mockAuth.isAuthenticated()).thenReturn(true);
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> orderService.createOrder(addressId));
    }

    @Test
    public void createOrder_Success() {
        Long addressId = 6L;

        AddressEntity address = new AddressEntity();
        address.setId(6L);

        UserEntity user = new UserEntity();
        user.setId(5L);
        user.setEmail("current@email.com");

        Product orangeJuice = new BeverageEntity();
        orangeJuice.setId(2L);
        orangeJuice.setProductType(ProductTypeEnum.BEVERAGE);
        orangeJuice.setName("Orange juice");
        orangeJuice.setPrice(5.00);

        CartItem orangeJuiceCartItem = new CartItem();
        orangeJuiceCartItem.setProduct(orangeJuice);
        orangeJuiceCartItem.setQuantity(1);
        orangeJuiceCartItem.setPrice(5.00);
        orangeJuiceCartItem.setId(7L);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(orangeJuiceCartItem);

        ShoppingCartEntity shoppingCart = new ShoppingCartEntity();
        shoppingCart.setCartItems(cartItems);
        user.setCart(shoppingCart);
        Authentication mockAuth = mock();

        when(authenticationFacade.getAuthentication()).thenReturn(mockAuth);
        when(mockAuth.getName()).thenReturn(user.getEmail());
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(userRepository.findUserWithOrders(user.getEmail())).thenReturn(Optional.of(user));

        orderService.createOrder(addressId);

        verify(orderRepository, times(1)).save(any());
    }
}