package yummydelivery.server.exceptions;

import org.springframework.http.HttpStatus;

public class AddressNotFoundException extends ApiException {

    public AddressNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
