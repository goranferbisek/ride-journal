package si.ferbisek.ride_journal.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class LoginResponse {

    private String jwtToken;
    private long expiresAt;
}
