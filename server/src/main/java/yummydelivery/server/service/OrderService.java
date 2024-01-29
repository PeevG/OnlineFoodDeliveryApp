package yummydelivery.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import yummydelivery.server.enums.OrderStatusEnum;
import yummydelivery.server.exceptions.AddressNotFoundException;
import yummydelivery.server.exceptions.UserNotFoundException;
import yummydelivery.server.model.*;
import yummydelivery.server.repository.AddressRepository;
import yummydelivery.server.repository.OrderRepository;
import yummydelivery.server.repository.UserRepository;
import yummydelivery.server.security.AuthenticationFacade;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AuthenticationFacade authenticationFacade;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, AddressRepository addressRepository, AuthenticationFacade authenticationFacade) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.authenticationFacade = authenticationFacade;
    }

    public void createOrder(Long addressId) {
        authenticationFacade.checkIfUserIsAuthenticated();

        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(HttpStatus.NOT_FOUND, "Address not found"));

        UserEntity currentUser = getCurrentUser();
        OrderEntity newOrder = createNewOrder(address, currentUser);

        orderRepository.save(newOrder);
        currentUser.getOrders().add(newOrder);
        userRepository.save(currentUser);
    }
    //TODO fix this method
    @Scheduled(fixedDelay = 60000)
    public void updateOrderStatus() {
        List<OrderEntity> processingOrders = orderRepository
                .findAllByStatusOrderByCreatedOnDesc(OrderStatusEnum.PROCESSING);
        if (!processingOrders.isEmpty()) {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            processingOrders
                    .forEach(o -> {
                        if (o.getCreatedOn().isBefore(oneHourAgo) || o.getCreatedOn().isEqual(oneHourAgo)) {
                            o.setStatus(OrderStatusEnum.DELIVERED);
                        }
                    });
        }
    }

    private UserEntity getCurrentUser() {
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
        List<Product> cartProducts = currentUser.getCart()
                .getCartItems()
                .stream()
                .map(CartItem::getProduct)
                .toList();
        newOrder.setOrderedProducts(cartProducts);
        return newOrder;
    }
}
