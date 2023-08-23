package yummydelivery.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import yummydelivery.server.model.AddressEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SignUpDTO {

    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    private String password;

    @Size(min = 2, max = 15, message = "First name must be between 2 and 15 characters")
    private String firstName;

    @Size(min = 2, max = 15, message = "Last name must be between 2 and 15 characters")
    private String lastName;

    @NotNull(message = "Address is required")
    private AddressEntity address;
}
