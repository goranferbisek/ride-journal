package si.ferbisek.ride_journal.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import si.ferbisek.ride_journal.dto.request.RegistrationRequest;
import si.ferbisek.ride_journal.dto.response.RegistrationResponse;
import si.ferbisek.ride_journal.entity.User;
import si.ferbisek.ride_journal.service.AuthService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());
        User registeredUser = authService.register(registrationRequest.getUsername(), encodedPassword);

        RegistrationResponse registrationResponse = new RegistrationResponse();
        registrationResponse.setId(registeredUser.getId());
        registrationResponse.setUsername(registeredUser.getUsername());

        return ResponseEntity.created(null).body(registrationResponse);
    }


}
