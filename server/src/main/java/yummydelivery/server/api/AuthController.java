package yummydelivery.server.api;

import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import yummydelivery.server.dto.JwtResponseDTO;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.dto.SignInDTO;
import yummydelivery.server.dto.SignUpDTO;
import yummydelivery.server.service.AuthService;

import java.util.stream.Collectors;

import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController
@RequestMapping(path = API_BASE)
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ResponseDTO<Object>> signUpUser(@Valid @RequestBody SignUpDTO signUpDTO,
                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ResponseDTO
                                    .builder()
                                    .message(errorMessages)
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .build()
                    );
        }
        authService.signUpUser(signUpDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ResponseDTO
                                .builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message("Signed up successfully")
                                .build()
                );
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseDTO<JwtResponseDTO>> signInUser(@Valid @RequestBody SignInDTO signInDTO) {

        String token = authService.signInUser(signInDTO);
        JwtResponseDTO jwtResponseDTO = new JwtResponseDTO(token, "Bearer");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<JwtResponseDTO>builder()
                                .statusCode(HttpStatus.OK.value())
                                .content(jwtResponseDTO)
                                .message("User logged successfully")
                                .build()
                );
    }
}
