package si.ferbisek.ride_journal.dto.response;

import lombok.*;
import si.ferbisek.ride_journal.dto.UserDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class LoginResponse {

    private String jwtToken;
    private long expiresAt;
    private UserDto user;
}
