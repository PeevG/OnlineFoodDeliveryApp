package yummydelivery.server.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    private String name;

    @NotNull(message = "Weight is required")
    private int weight;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price is required and must be greater than 0")
    private double price;

    @NotNull
    private FoodTypeEnum foodTypeEnum;
    private String imageURL;
    private List<String> ingredients;
}
