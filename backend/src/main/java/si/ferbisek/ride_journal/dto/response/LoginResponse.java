package si.ferbisek.ride_journal.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class LoginResponse {

    private String token;
    private long expiresIn;
}
