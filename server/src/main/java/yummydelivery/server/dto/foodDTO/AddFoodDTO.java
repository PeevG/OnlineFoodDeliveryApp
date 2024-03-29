package yummydelivery.server.dto.foodDTO;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yummydelivery.server.enums.FoodTypeEnum;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddFoodDTO {

    @NotNull(message = "Name is required")
    @Size(min = 2, max = 30, message = "Food name must be between 2 and 30 characters")
    private String name;

    @NotNull(message = "Weight is required")
    private int grams;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price is required and must be greater than 0")
    private double price;

    @NotNull
    private FoodTypeEnum foodTypeEnum;
    private List<String> ingredients;
}
