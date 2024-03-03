package yummydelivery.server.dto.BeverageDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BeverageDTO {
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name must be minimum 3 characters")
    private String name;
    @NotNull(message = "Milliliters field is required")
    @Min(value = 15, message = "Milliliters must be greater than or equal to 15")
    private int milliliters;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price is required and must be greater than 0")
    private double price;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BeverageDTO that = (BeverageDTO) object;
        return milliliters == that.milliliters && Double.compare(price, that.price) == 0 && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, milliliters, price);
    }
}
