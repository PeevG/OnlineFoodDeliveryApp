package yummydelivery.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import yummydelivery.server.enums.FoodTypeEnum;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "foods")
@Entity
@Builder
public class FoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Weight is required")
    private int weight;
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price is required and must be greater than 0")
    private double price;
    @Enumerated(EnumType.STRING)
    private FoodTypeEnum foodTypeEnum;
    private String imageURL;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "foods_ingredients",
         joinColumns = @JoinColumn(name = "food_id"))
    @Column(name = "ingredient")
    private List<String> ingredients;

}
