package yummydelivery.server.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.dto.ShoppingCartDTO;
import yummydelivery.server.service.CartService;

import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController()
@RequestMapping(API_BASE)
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @Operation(summary = "Add new item to user shopping cart by Id")
    @PostMapping("/menu/addToCart/{productId}")
    public ResponseEntity<ResponseDTO<String>> addItemToCart(@PathVariable(name = "productId") Long productId) {
        cartService.addItemToShoppingCart(productId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<String>builder()
                                .statusCode(HttpStatus.OK.value())
                                .body("Product with id " + productId + " is added to cart successfully")
                                .build()
                );
    }
    @Operation(summary = "Remove item from user shopping cart by item Id")
    @DeleteMapping("/shoppingCart/{cartItemId}")
    public ResponseEntity<ResponseDTO<String>> removeItemFromCart(@PathVariable(name = "cartItemId") Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<String>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Cart item is removed successfully")
                                .build()
                );
    }
    @Operation(summary = "Get user shopping cart")
    @GetMapping("/shoppingCart")
    public ResponseEntity<ResponseDTO<ShoppingCartDTO>> getUserCart(){
        ShoppingCartDTO userCart = cartService.getUserCart();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<ShoppingCartDTO>builder()
                                .statusCode(HttpStatus.OK.value())
                                .body(userCart)
                                .build()
                );
    }
}
