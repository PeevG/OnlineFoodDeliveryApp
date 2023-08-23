package yummydelivery.server.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressDTO {
    @NotNull(message = "Email is required")
    private String city;
    @NotNull(message = "Email is required")
    private String street;
    @NotNull(message = "Email is required")
    private int streetNumber;
    @NotNull(message = "Email is required")
    private int phoneNumber;
}
