package yummydelivery.server.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yummydelivery.server.dto.foodDTO.AddFoodDTO;
import yummydelivery.server.dto.foodDTO.FoodDTO;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.dto.foodDTO.UpdateFoodDTO;
import yummydelivery.server.service.FoodService;
import yummydelivery.server.utils.CommonUtils;



import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController()
@RequestMapping(API_BASE + "/foods")
public class FoodController {
    private final FoodService foodService;
    private final CommonUtils utils;

    public FoodController(FoodService foodService, CommonUtils utils) {
        this.foodService = foodService;
        this.utils = utils;
    }

    @Operation(summary = "Get food by Id")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<FoodDTO>> getFoodById(@PathVariable Long id) {
        FoodDTO foodDTO = foodService.getFoodById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<FoodDTO>builder()
                                .statusCode(HttpStatus.OK.value())
                                .body(foodDTO)
                                .build()
                );
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Add new food. Admin role required!")
    @PostMapping()
    public ResponseEntity<ResponseDTO<Void>> addFood(@Valid @RequestPart("productInfo") AddFoodDTO addFoodDTO,
                                                     @RequestPart(required = false) MultipartFile productImage,
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
        foodService.addFood(addFoodDTO, productImage);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message(addFoodDTO.getName() + " is added successfully")
                                .build()
                );
    }

    @Operation(summary = "Get all foods by food type (Paginated)")
    @GetMapping()
    public ResponseEntity<ResponseDTO<Page<FoodDTO>>> getFoodsByType(@RequestParam String foodType,
                                                                     @RequestParam(defaultValue = "0") int page) {

        Page<FoodDTO> foodsByType = foodService.getAllFoodsByType(foodType, page);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Page<FoodDTO>>builder()
                                .statusCode(HttpStatus.OK.value())
                                .body(foodsByType)
                                .build()
                );
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete food by Id. Admin role required!")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteFood(@PathVariable Long id) {
        foodService.deleteFoodOrBeverage(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Food with id " + id + " is deleted successfully")
                                .build()
                );
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update food by Id. Admin role required!")
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> updateFood(@PathVariable Long id,
                                                        @Valid @RequestPart UpdateFoodDTO updateFoodDTO,
                                                        @RequestPart(required = false) MultipartFile productImage,
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
        foodService.updateFood(id, updateFoodDTO, productImage);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .message("Food with id " + id + " is updated successfully")
                                .statusCode(HttpStatus.CREATED.value())
                                .build()
                );
    }
}
