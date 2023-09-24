package yummydelivery.server.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class FoodDTO {
    private Long id;
    private String name;
    private int weight;
    private double price;
    @Enumerated(EnumType.ORDINAL)
    private FoodTypeEnum foodTypeEnum;
    private String imageURL;
    private List<String> ingredients;
}
