package yummydelivery.server.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yummydelivery.server.dto.view.OrderView;
import yummydelivery.server.enums.OrderStatusEnum;
import yummydelivery.server.exceptions.AddressNotFoundException;
import yummydelivery.server.exceptions.UserNotFoundException;
import yummydelivery.server.model.*;
import yummydelivery.server.repository.AddressRepository;
import yummydelivery.server.repository.CartRepository;
import yummydelivery.server.repository.OrderRepository;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.AuthenticationFacade;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AuthenticationFacade authenticationFacade;
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, AddressRepository addressRepository, AuthenticationFacade authenticationFacade, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.authenticationFacade = authenticationFacade;
        this.modelMapper = modelMapper;
    }

    public void createOrder(Long addressId) {
        authenticationFacade.checkIfUserIsAuthenticated();

        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(HttpStatus.NOT_FOUND, "Address not found"));

        UserEntity currentUser = getCurrentUserWithOrders();
        OrderEntity newOrder = createNewOrder(address, currentUser);
        orderRepository.save(newOrder);

        saveUpdatedUserWithNewOrder(currentUser, newOrder);
        clearUserShoppingCart(currentUser);
    }

    public List<OrderView> getUserOrders() {
        UserEntity currentUser = getCurrentUserWithOrders();
        return currentUser
                .getOrders()
                .stream()
                .map(o -> modelMapper.map(o, OrderView.class)).toList();
    }

    private static void clearUserShoppingCart(UserEntity currentUser) {
        currentUser.getCart().getCartItems().clear();
        currentUser.getCart().setCartPrice(0.0);
    }

    private UserEntity getCurrentUserWithOrders() {
        String username = authenticationFacade.getAuthentication().getName();
        return userRepository
                .findUserWithOrders(username)
                .orElseThrow(() -> new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private OrderEntity createNewOrder(AddressEntity address, UserEntity currentUser) {
        OrderEntity newOrder = new OrderEntity();
        newOrder.setOrderCost(currentUser.getCart().getCartPrice());
        newOrder.setDeliveryAddress(address);
        newOrder.setStatus(OrderStatusEnum.PROCESSING);
        List<CartItem> cartProducts = currentUser.getCart()
                .getCartItems();
        newOrder.setOrderedProducts(cartProducts);
        return newOrder;
    }

    private void saveUpdatedUserWithNewOrder(UserEntity currentUser, OrderEntity newOrder) {
        List<OrderEntity> uOrders = currentUser.getOrders();
        uOrders.add(newOrder);
        currentUser.setOrders(uOrders);
        userRepository.save(currentUser);
    }

    @Scheduled(fixedDelay = 600000)
    private void updateOrderStatus() {
        List<OrderEntity> processingOrders = orderRepository
                .findAllByStatusOrderByCreatedOnDesc(OrderStatusEnum.PROCESSING);
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        for (OrderEntity o : processingOrders) {
            boolean createMoreThanOneHour = o.getCreatedOn().isBefore(oneHourAgo);
            if (createMoreThanOneHour) {
                o.setStatus(OrderStatusEnum.DELIVERED);
                orderRepository.save(o);
                log.atInfo().log("Order status is changed because 1 hour has passed since creation time");
            }
        }
    }
}
