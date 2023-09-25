package yummydelivery.server.api;

import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import yummydelivery.server.dto.BeverageDTO.AddOrUpdateBeverageDTO;
import yummydelivery.server.dto.BeverageDTO.BeverageDTO;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.service.BeverageService;

import java.util.stream.Collectors;

import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController
@RequestMapping(API_BASE + "/beverages")
public class BeverageController {
    private final BeverageService beverageService;

    public BeverageController(BeverageService beverageService) {
        this.beverageService = beverageService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<BeverageDTO>> getBeverageById(@PathVariable Long id) {
        BeverageDTO beverageDTO = beverageService.getBeverage(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<BeverageDTO>builder()
                                .statusCode(HttpStatus.OK.value())
                                .content(beverageDTO)
                                .build()
                );
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> addBeverage(@Valid @RequestBody AddOrUpdateBeverageDTO addBeverageDTO,
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
        beverageService.addBeverage(addBeverageDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message(addBeverageDTO.getName() + " was added successfully")
                                .build()
                );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> updateBeverage(@PathVariable Long id,
                                                            @Valid @RequestBody AddOrUpdateBeverageDTO dto,
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
        beverageService.updateBeverage(id, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message("Beverage with id " + id + " was updated successfully")
                                .build()
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteBeverage(@PathVariable Long id) {
        beverageService.deleteBeverage(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .message("Beverage with id " + id + " was deleted successfully")
                                .statusCode(HttpStatus.OK.value())
                                .build()
                );
    }
}
