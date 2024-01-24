package yummydelivery.server.model;

import jakarta.persistence.Entity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class BeverageEntity extends Product {
    private int milliliters;
}

