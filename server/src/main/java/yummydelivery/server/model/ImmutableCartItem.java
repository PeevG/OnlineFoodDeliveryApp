package yummydelivery.server.model;

import jakarta.persistence.*;
import lombok.Data;
import yummydelivery.server.dto.view.ProductView;

import java.io.Serializable;

@Embeddable
@Data
public class ImmutableCartItem implements Serializable {
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "product_id")),
            @AttributeOverride(name = "name", column = @Column(name = "product_name")),
            @AttributeOverride(name = "price", column = @Column(name = "product_price")),
            @AttributeOverride(name = "imageURL", column = @Column(name = "product_image_url")),
            @AttributeOverride(name = "productType", column = @Column(name = "product_type"))
    })
    private ProductView product;
    private int quantity;
    private double price;
}
