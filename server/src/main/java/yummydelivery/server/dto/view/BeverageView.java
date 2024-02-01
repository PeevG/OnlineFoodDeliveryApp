package yummydelivery.server.dto.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BeverageView {
    private Long id;
    private String name;
    private double price;
    private String imageURL;
    private int milliliters;
}
