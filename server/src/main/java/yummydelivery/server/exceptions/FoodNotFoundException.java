package yummydelivery.server.exceptions;

import org.springframework.http.HttpStatus;

public class FoodNotFoundException extends ApiException{

    public FoodNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
