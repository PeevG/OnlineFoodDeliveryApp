package yummydelivery.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yummydelivery.server.model.CartItem;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShoppingCartDTO {
    private List<CartItem> items = new ArrayList<>();
    private double cartPrice;
}
