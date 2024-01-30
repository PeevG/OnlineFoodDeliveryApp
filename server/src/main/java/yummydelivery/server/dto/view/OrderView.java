package yummydelivery.server.dto.view;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yummydelivery.server.enums.OrderStatusEnum;
import yummydelivery.server.model.AddressEntity;
import yummydelivery.server.model.CartItem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderView {

    private Long id;
    private LocalDateTime createdOn;
    private double orderCost;
    @Enumerated
    private OrderStatusEnum status;
    private List<CartItemView> orderedProducts = new ArrayList<>();
    private AddressView deliveryAddress;
}
