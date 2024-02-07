package yummydelivery.server.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import yummydelivery.server.dto.AddressDTO;
import yummydelivery.server.dto.view.AddressView;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.service.AddressService;
import yummydelivery.server.utils.CommonUtils;

import java.util.stream.Collectors;

import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController
@RequestMapping(API_BASE + "/addresses")
public class AddressController {
    private final AddressService addressService;
    private final CommonUtils utils;

    public AddressController(AddressService addressService, CommonUtils utils) {
        this.addressService = addressService;
        this.utils = utils;
    }

    @Operation(summary = "Get all user addresses (Paginated)")
    @GetMapping
    public ResponseEntity<ResponseDTO<Page<AddressView>>> getUserAddresses(@RequestParam(defaultValue = "0") int page) {
        Page<AddressView> userAddresses = addressService.getUserAddresses(page);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Page<AddressView>>builder()
                                .statusCode(HttpStatus.OK.value())
                                .body(userAddresses)
                                .build()
                );
    }

    @Operation(summary = "Add new user address")
    @PostMapping()
    public ResponseEntity<ResponseDTO<AddressDTO>> addNewAddress(@Valid @RequestBody AddressDTO addressDTO,
                                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = utils.collectErrorMessagesToString(bindingResult);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ResponseDTO
                                    .<AddressDTO>builder()
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .message(errors)
                                    .build()
                    );
        }
        AddressDTO createdAddress = addressService.addNewAddress(addressDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ResponseDTO
                                .<AddressDTO>builder()
                                .body(createdAddress)
                                .statusCode(HttpStatus.CREATED.value())
                                .build()
                );
    }

    @Operation(summary = "Update user address by address Id")
    @PutMapping("/{addressId}")
    public ResponseEntity<ResponseDTO<AddressDTO>> updateAddressById(@Valid @RequestBody AddressDTO addressDTO,
                                                                     @PathVariable(name = "addressId") Long addressId,
                                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = utils.collectErrorMessagesToString(bindingResult);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            ResponseDTO
                                    .<AddressDTO>builder()
                                    .statusCode(HttpStatus.BAD_REQUEST.value())
                                    .message(errors)
                                    .build()
                    );
        }
        AddressDTO updatedAddress = addressService.updateAddress(addressDTO, addressId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<AddressDTO>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Address with id " + addressId + " is updated")
                                .body(updatedAddress)
                                .build()
                );
    }

    @Operation(summary = "Delete user address by Id")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<ResponseDTO<Void>> deleteAddressById(@PathVariable(name = "addressId") Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Address with id " + addressId + " successfully deleted")
                                .build()
                );
    }
}
