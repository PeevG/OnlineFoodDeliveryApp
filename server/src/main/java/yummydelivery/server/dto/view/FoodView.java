package yummydelivery.server.dto.view;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yummydelivery.server.enums.FoodTypeEnum;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FoodView extends ProductView {
    private int grams;
    @Enumerated(EnumType.STRING)
    private FoodTypeEnum foodTypeEnum;
    private List<String> ingredients = new ArrayList<>();
}
