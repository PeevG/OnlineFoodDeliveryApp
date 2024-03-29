package yummydelivery.server.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.dto.view.OrderView;
import yummydelivery.server.service.OrderService;

import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController
@RequestMapping(API_BASE + "/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @Operation(summary = "Create new order by address Id")
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
    @Operation(summary = "Get all user orders (Paginated)")
    @GetMapping()
    public ResponseEntity<ResponseDTO<Page<OrderView>>> getUserOrders(@RequestParam(defaultValue = "0") int page) {
        Page<OrderView> userOrders = orderService.getUserOrders(page);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Page<OrderView>>builder()
                                .message("User orders retrieved successfully")
                                .statusCode(HttpStatus.OK.value())
                                .body(userOrders)
                                .build()
                );
    }
}
