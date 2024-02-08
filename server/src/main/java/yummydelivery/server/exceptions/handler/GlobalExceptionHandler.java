package yummydelivery.server.exceptions.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.exceptions.ApiException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<Object> handleCustomExceptions(ApiException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(
                        ResponseDTO
                                .builder()
                                .message(ex.getMessage())
                                .statusCode(ex.getStatus().value())
                                .body(null)
                                .build()
                );
    }
    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentExceptions(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ResponseDTO
                                .builder()
                                .message(ex.getMessage())
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .body(null)
                                .build()
                );
    }
}
