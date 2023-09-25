package yummydelivery.server.exceptions;

import org.springframework.http.HttpStatus;

public class BeverageNotFoundException extends ApiException{
    public BeverageNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
