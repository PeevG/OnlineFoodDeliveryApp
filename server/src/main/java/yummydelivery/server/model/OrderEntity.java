package yummydelivery.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.scheduling.annotation.Scheduled;
import yummydelivery.server.enums.OrderStatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Data
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Order cost is required and must be greater than 0")
    private double orderCost;
    @Enumerated
    private OrderStatusEnum status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "order_cart_items",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "cart_item_id"))
    private List<CartItem> orderedProducts = new ArrayList<>();
    @ManyToOne(fetch = FetchType.EAGER)
    private AddressEntity deliveryAddress;
}
