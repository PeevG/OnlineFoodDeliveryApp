package yummydelivery.server.dto.view;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yummydelivery.server.enums.ProductTypeEnum;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductView {
    private Long id;
    private String name;
    private double price;
    private String imageURL;
    @Enumerated(EnumType.STRING)
    private ProductTypeEnum productType;
}
