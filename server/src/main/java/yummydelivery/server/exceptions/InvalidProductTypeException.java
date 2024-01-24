package yummydelivery.server.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidProductTypeException extends ApiException{
    public InvalidProductTypeException(HttpStatus status, String message) {
        super(status, message);
    }
}
