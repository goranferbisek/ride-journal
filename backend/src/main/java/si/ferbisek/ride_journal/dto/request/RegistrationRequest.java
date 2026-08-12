package si.ferbisek.ride_journal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
public class RegistrationRequest {

    @NotBlank(message = "Username is required")
    @Length(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    // password regex would be nice
    private String password;
}
