package yummydelivery.server.model;

import jakarta.persistence.*;
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
    private String name;
    private int weight;
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
