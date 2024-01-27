package yummydelivery.server.api;

import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import yummydelivery.server.dto.AddressDTO;
import yummydelivery.server.dto.AddressView;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.service.AddressService;

import java.util.List;
import java.util.stream.Collectors;

import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController
@RequestMapping(API_BASE + "/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<AddressView>>> getUserAddresses() {
        List<AddressView> userAddresses = addressService.getUserAddresses();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<List<AddressView>>builder()
                                .statusCode(HttpStatus.OK.value())
                                .body(userAddresses)
                                .build()
                );
    }

    @PostMapping()
    public ResponseEntity<ResponseDTO<AddressDTO>> addNewAddress(@Valid @RequestBody AddressDTO addressDTO,
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

    @PutMapping("/{addressId}")
    public ResponseEntity<ResponseDTO<AddressDTO>> updateAddressById(@Valid @RequestBody AddressDTO addressDTO,
                                                                     @PathVariable(name = "addressId") Long addressId) {
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
