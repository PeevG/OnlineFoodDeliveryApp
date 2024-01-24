package yummydelivery.server.api;

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

    @PostMapping("/menu/addToCart/{foodId}")
    public ResponseEntity<ResponseDTO<String>> addItemToCart(@PathVariable(name = "foodId") Long foodId) {
        cartService.addItemToShoppingCart(foodId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<String>builder()
                                .statusCode(HttpStatus.OK.value())
                                .body("Food with id " + foodId + " is added to cart successfully")
                                .build()
                );
    }

    @DeleteMapping("/shoppingCart/{cartItemId}")
    public ResponseEntity<ResponseDTO<String>> removeItemFromCart(@PathVariable(name = "cartItemId") Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<String>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Food cart item is removed successfully")
                                .build()
                );
    }

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
