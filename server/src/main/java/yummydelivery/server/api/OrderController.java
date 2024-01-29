package yummydelivery.server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.service.OrderService;

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

    // 3. Show user orders
}
