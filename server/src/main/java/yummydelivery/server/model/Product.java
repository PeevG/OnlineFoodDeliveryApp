package yummydelivery.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import yummydelivery.server.enums.ProductTypeEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Entity
@Table(name = "products")
public abstract class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    @Column(unique = true)
    @Size(min = 2, max = 30, message = "Product name must be between 2 and 30 characters")
    private String name;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price is required and must be greater than 0")
    private double price;
    private String imageURL;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductTypeEnum productType;
}
