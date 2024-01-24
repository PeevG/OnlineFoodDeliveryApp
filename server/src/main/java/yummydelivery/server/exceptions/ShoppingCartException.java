package yummydelivery.server.exceptions;

import org.springframework.http.HttpStatus;

public class ShoppingCartException extends ApiException {

    public ShoppingCartException(HttpStatus status, String message) {
        super(status, message);
    }
}
