package yummydelivery.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Data
@Builder
@Table(name = "addresses")
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 25, message = "City name must be minimum 2 and maximum 25 characters")
    private String city;

    @NotBlank(message = "Street name is required")
    @Size(min = 2, max = 25, message = "Street name must be minimum 2 and maximum 25 characters")
    private String streetName;

    @NotNull(message = "Street number is required")
    private String streetNumber;

    @Pattern(
            regexp = "^\\+?[0-9]{5,20}$",
            message = "Invalid phone number format"
    )
    @Size(max = 20, message = "Phone number must be at most 20 characters")
    @NotNull(message = "Phone number is required")
    private String phoneNumber;
}


