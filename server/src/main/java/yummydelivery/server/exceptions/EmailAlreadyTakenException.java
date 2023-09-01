package yummydelivery.server.exceptions;

import org.springframework.http.HttpStatus;

public class EmailAlreadyTakenException extends ApiException{
    public EmailAlreadyTakenException(HttpStatus status, String message) {
        super(status, message);
    }
}
