package yummydelivery.server.dto.BeverageDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddOrUpdateBeverageDTO {
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name must be minimum 3 characters")
    private String name;
    @NotNull(message = "Milliliters field is required")
    @Min(value = 15, message = "Milliliters must be greater than or equal to 15")
    private int milliliters;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price is required and must be greater than 0")
    private double price;
}
