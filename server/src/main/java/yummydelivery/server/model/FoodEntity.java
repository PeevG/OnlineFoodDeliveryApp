package yummydelivery.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import yummydelivery.server.enums.FoodTypeEnum;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class FoodEntity extends Product {

    @NotNull(message = "Grams is required")
    private int grams;
    @Enumerated(EnumType.STRING)
    private FoodTypeEnum foodTypeEnum;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "products_foods_ingredients",
            joinColumns = @JoinColumn(name = "food_id"))
    @Column(name = "ingredient")
    private List<String> ingredients = new ArrayList<>();
}
