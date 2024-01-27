package yummydelivery.server.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import yummydelivery.server.model.AddressEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignUpDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 6, message = "Password size must be minimum 6 characters")
    private String password;

    @Size(min = 2, max = 15, message = "First name must be between 2 and 15 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 15, message = "Last name must be between 2 and 15 characters")
    private String lastName;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 25, message = "City name must be minimum 2 and maximum 25 characters")
    private String city;

    @NotBlank(message = "Street name is required")
    @Size(min = 2, max = 25, message = "Street name must be minimum 2 and maximum 25 characters")
    private String streetName;

    @NotNull(message = "Street number is required")
    private String streetNumber;

    @Pattern(
            regexp = "^\\+?[0-9]{5,15}$",
            message = "Invalid phone number format"
    )
    @Size(max = 20, message = "Phone number must be at most 15 characters")
    private String phoneNumber;
}
