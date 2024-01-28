package yummydelivery.server.api;

import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.dto.UpdatePasswordDTO;
import yummydelivery.server.service.UserService;

import java.util.stream.Collectors;

import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController
@RequestMapping(API_BASE)
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO<Void>> changeUserPassword(@PathVariable(name = "userId") Long userId,
                                                                             @Valid @RequestBody UpdatePasswordDTO userInfoDTO,
                                                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult
                    .getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ResponseDTO
                                    .<Void>builder()
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .message(errors)
                                    .build()
                    );
        }
        userService.updateUserInfo(userInfoDTO, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("User password is changed successfully")
                                .build()
                );
    }
}
