package yummydelivery.server.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(HttpStatus status, String message) {
        super(status, message);
    }
}
