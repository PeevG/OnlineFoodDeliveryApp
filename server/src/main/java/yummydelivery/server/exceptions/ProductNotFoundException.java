package yummydelivery.server.exceptions;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends ApiException {

    public ProductNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
