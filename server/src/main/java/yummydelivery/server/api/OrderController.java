package yummydelivery.server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yummydelivery.server.dto.view.OrderView;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.service.OrderService;

import java.util.List;

import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController
@RequestMapping(API_BASE + "/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{addressId}")
    public ResponseEntity<ResponseDTO<Void>> createOrderForSelectedAddress(@PathVariable(name = "addressId") Long addressId) {
        orderService.createOrder(addressId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Order created successfully")
                                .build()
                );
    }

    @GetMapping()
    public ResponseEntity<ResponseDTO<List<OrderView>>> getUserOrders() {
        List<OrderView> userOrders = orderService.getUserOrders();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<List<OrderView>>builder()
                                .message("User orders retrieved successfully")
                                .statusCode(HttpStatus.OK.value())
                                .body(userOrders)
                                .build()
                );
    }
}
