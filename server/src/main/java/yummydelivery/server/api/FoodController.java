package yummydelivery.server.api;

import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import yummydelivery.server.dto.foodDTO.AddFoodDTO;
import yummydelivery.server.dto.foodDTO.FoodDTO;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.dto.foodDTO.UpdateFoodDTO;
import yummydelivery.server.service.FoodService;

import java.util.List;
import java.util.stream.Collectors;

import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController()
@RequestMapping(API_BASE + "/foods")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<FoodDTO>> getFoodById(@PathVariable Long id) {
        FoodDTO foodDTO = foodService.getFood(id);
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

    @PostMapping()
    public ResponseEntity<ResponseDTO<Void>> addFood(@Valid @RequestBody AddFoodDTO addFoodDTO,
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
        foodService.addFood(addFoodDTO);
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

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> updateFood(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateFoodDTO updateFoodDTO,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult
                    .getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(";"));
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
        foodService.updateFood(id, updateFoodDTO);
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
