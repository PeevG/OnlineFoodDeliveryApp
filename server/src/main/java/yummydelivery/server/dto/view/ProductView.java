package yummydelivery.server.dto.view;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yummydelivery.server.enums.ProductTypeEnum;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductView implements Serializable {
    private Long id;
    private String name;
    private double price;
    private String imageURL;
    @Enumerated(EnumType.STRING)
    private ProductTypeEnum productType;
}
