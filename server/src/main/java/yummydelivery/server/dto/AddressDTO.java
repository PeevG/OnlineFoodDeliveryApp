package yummydelivery.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressDTO {

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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AddressDTO that = (AddressDTO) object;
        return Objects.equals(city, that.city) && Objects.equals(streetName, that.streetName) && Objects.equals(streetNumber, that.streetNumber) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, streetName, streetNumber, phoneNumber);
    }
}
