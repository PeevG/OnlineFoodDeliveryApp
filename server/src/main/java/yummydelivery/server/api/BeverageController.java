package yummydelivery.server.api;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yummydelivery.server.dto.BeverageDTO.BeverageDTO;
import yummydelivery.server.dto.ResponseDTO;
import yummydelivery.server.dto.view.BeverageView;
import yummydelivery.server.service.BeverageService;
import yummydelivery.server.service.FoodService;
import io.swagger.v3.oas.annotations.*;
import yummydelivery.server.utils.CommonUtils;


import static yummydelivery.server.config.ApplicationConstants.API_BASE;

@RestController
@RequestMapping(API_BASE + "/beverages")
public class BeverageController {
    private final BeverageService beverageService;
    private final FoodService foodService;
    private final CommonUtils utils;

    public BeverageController(BeverageService beverageService, FoodService foodService, CommonUtils utils) {
        this.beverageService = beverageService;
        this.foodService = foodService;
        this.utils = utils;
    }

    @Operation(summary = "Get Beverage by Id",
            description = "Retrieve details of a specific beverage based on its unique identifier.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<BeverageView>> getBeverageById(@PathVariable Long id) {
        BeverageView beverageView = beverageService.getBeverageById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<BeverageView>builder()
                                .statusCode(HttpStatus.OK.value())
                                .body(beverageView)
                                .build()
                );
    }

    @Operation(summary = "Get all beverages from the menu (Paginated)")
    @GetMapping
    public ResponseEntity<ResponseDTO<Page<BeverageView>>> getAllBeverages(@RequestParam(defaultValue = "0") int page) {
        Page<BeverageView> beverageDTOS = beverageService.getAllBeverages(page - 1);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Page<BeverageView>>builder()
                                .statusCode(HttpStatus.OK.value())
                                .body(beverageDTOS)
                                .build()
                );
    }


    @Operation(summary = "Adding beverage to the menu. Admin role required!",
            description = "The content-Type of the JSON object must be configured as application/json. " +
                    "Upload product image to Cloudinary and set CloudinaryURL as imageURL")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseDTO<Void>> addBeverage(@RequestPart("beverageDTO")
                                                         @Valid
                                                         BeverageDTO beverageDTO,
                                                         @RequestPart(value = "productImage", required = false)
                                                         MultipartFile productImage,
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
        beverageService.addBeverage(beverageDTO, productImage);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message(beverageDTO.getName() + " is added successfully")
                                .build()
                );
    }

    @Operation(summary = "Update beverage by Id. Admin role required!",
            description = "The content-Type of the JSON object must be configured as application/json. " +
                    "If new product image is provided its uploaded to Cloudinary and beverage imageURL is updated")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO<Void>> updateBeverage(@PathVariable Long id,
                                                            @Valid @RequestPart BeverageDTO dto,
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
        beverageService.updateBeverage(id, dto, productImage);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .statusCode(HttpStatus.CREATED.value())
                                .message("Beverage with id " + id + " is updated successfully")
                                .build()
                );
    }

    @Operation(summary = "Delete beverage by Id. Admin role required!")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteBeverage(@PathVariable Long id) {
        foodService.deleteFoodOrBeverage(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ResponseDTO
                                .<Void>builder()
                                .message("Beverage with id " + id + " is deleted successfully")
                                .statusCode(HttpStatus.OK.value())
                                .build()
                );
    }
}
