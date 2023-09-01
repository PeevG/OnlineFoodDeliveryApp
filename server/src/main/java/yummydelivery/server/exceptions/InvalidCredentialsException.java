package yummydelivery.server.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException{
    public InvalidCredentialsException(HttpStatus status, String message) {
        super(status, message);
    }
}
