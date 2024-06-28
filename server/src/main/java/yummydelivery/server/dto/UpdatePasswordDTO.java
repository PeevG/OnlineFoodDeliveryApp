package yummydelivery.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDTO {

    @NotNull(message = "Password is required")
    private String oldPassword;

    @NotNull(message = "New password is required")
    @Size(min = 6, message = "New password size must be minimum 6 characters")
    private String newPassword;

    @NotNull(message = "Repeat new password is required")
    @Size(min = 6, message = "Repeat new password size must be minimum 6 characters")
    private String repeatNewPassword;
}
