package yummydelivery.server.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yummydelivery.server.dto.ShoppingCartDTO;
import yummydelivery.server.exceptions.ProductNotFoundException;
import yummydelivery.server.exceptions.ShoppingCartException;
import yummydelivery.server.model.*;
import yummydelivery.server.repository.*;
import yummydelivery.server.security.AuthenticationFacade;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CartItemRepository cartItemRepository;
    private final AuthenticationFacade authenticationFacade;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, UserService userService, CartItemRepository cartItemRepository, AuthenticationFacade authenticationFacade, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.cartItemRepository = cartItemRepository;
        this.authenticationFacade = authenticationFacade;
        this.productRepository = productRepository;
    }

    public void addItemToShoppingCart(Long productId) {
        authenticationFacade.checkIfUserIsAuthenticated();

        Product product = productRepository
                .findById(productId).orElseThrow(() -> new ProductNotFoundException(HttpStatus.NOT_FOUND, "Product not found"));

        UserEntity user = userService.getCurrentUserByUsername();
        ShoppingCartEntity shoppingCartEntity = user.getCart();
        CartItem cartItem = findItemInShoppingCart(shoppingCartEntity.getCartItems(), productId);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            shoppingCartEntity.getCartItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }
        cartItem.setPrice(cartItem.getQuantity() * product.getPrice());

        shoppingCartEntity.setCartPrice(getShoppingCartTotalPrice(shoppingCartEntity));
        cartItemRepository.save(cartItem);
        cartRepository.save(shoppingCartEntity);
    }

    public ShoppingCartDTO getUserCart() {
        ShoppingCartEntity shoppingCartEntity = userService.getCurrentUserByUsername().getCart();
        ShoppingCartDTO cartDTO = new ShoppingCartDTO();

        List<CartItem> cartItems = shoppingCartEntity.getCartItems();
        cartDTO.setItems(cartItems);
        cartDTO.setCartPrice(shoppingCartEntity.getCartPrice());
        return cartDTO;
    }

    @Transactional
    public void removeItemFromCart(Long cartItemId) {
        authenticationFacade.checkIfUserIsAuthenticated();

        UserEntity user = userService.getCurrentUserByUsername();
        CartItem itemToRemove = cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() -> new ShoppingCartException(HttpStatus.NOT_FOUND, "Cart Item not found"));

        ShoppingCartEntity userCart = user.getCart();
        userCart.getCartItems().remove(itemToRemove);

        userCart.setCartPrice(getShoppingCartTotalPrice(userCart));
        cartItemRepository.delete(itemToRemove);
        userRepository.save(user);
    }

    private CartItem findItemInShoppingCart(List<CartItem> items, Long productId) {
        CartItem cartItem = null;
        for (CartItem item : items) {
            if (item.getProduct().getId() == productId) {
                cartItem = item;
            }
        }
        return cartItem;
    }

    private double getShoppingCartTotalPrice(ShoppingCartEntity shoppingCart) {
        return shoppingCart.getCartItems().stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
    }
}
