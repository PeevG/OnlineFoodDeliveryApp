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
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateFoodDTO {
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        UpdateFoodDTO that = (UpdateFoodDTO) object;
        return grams == that.grams && Double.compare(price, that.price) == 0 && Objects.equals(name, that.name) && foodTypeEnum == that.foodTypeEnum && Objects.equals(ingredients, that.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, grams, price, foodTypeEnum, ingredients);
    }

    @Override
    public String toString() {
        return "UpdateFoodDTO{" +
                "name='" + name + '\'' +
                ", grams=" + grams +
                ", price=" + price +
                ", foodTypeEnum=" + foodTypeEnum +
                ", ingredients=" + ingredients +
                '}';
    }
}
