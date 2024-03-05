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
import java.util.Objects;

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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        OrderView orderView = (OrderView) object;
        return Double.compare(orderCost, orderView.orderCost) == 0 && Objects.equals(id, orderView.id) && Objects.equals(createdOn, orderView.createdOn) && status == orderView.status && Objects.equals(orderedProducts, orderView.orderedProducts) && Objects.equals(deliveryAddress, orderView.deliveryAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdOn, orderCost, status, orderedProducts, deliveryAddress);
    }
}
