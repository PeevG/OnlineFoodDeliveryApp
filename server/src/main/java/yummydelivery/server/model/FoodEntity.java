package yummydelivery.server.model;

import jakarta.persistence.*;
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
@Table(name = "foods")
@Entity
public class FoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int weight;
    private double price;
    @Enumerated(EnumType.STRING)
    private FoodTypeEnum foodTypeEnum;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "foods_ingredients",
       joinColumns = @JoinColumn(name = "food_id"),
       inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private List<IngredientEntity> ingredients;

}
