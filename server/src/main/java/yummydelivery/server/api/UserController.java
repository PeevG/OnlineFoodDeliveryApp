package yummydelivery.server.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.dto.UpdatePasswordDTO;
import yummydelivery.server.service.UserService;
import yummydelivery.server.utils.CommonUtils;

import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController
@RequestMapping(API_BASE)
public class UserController {
    private final UserService userService;
    private final CommonUtils utils;

    public UserController(UserService userService, CommonUtils utils) {
        this.userService = userService;
        this.utils = utils;
    }

    @Operation(summary = "Change user password")
    @PutMapping("/user")
    public ResponseEntity<ResponseDTO<Void>> changeUserPassword(@Valid @RequestBody UpdatePasswordDTO userInfoDTO,
                                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = utils.collectErrorMessagesToString(bindingResult);
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
        userService.updateUserPassword(userInfoDTO);
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
