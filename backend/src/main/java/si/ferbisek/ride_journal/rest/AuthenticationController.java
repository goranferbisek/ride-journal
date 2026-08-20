package si.ferbisek.ride_journal.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import si.ferbisek.ride_journal.dto.request.LoginRequest;
import si.ferbisek.ride_journal.dto.request.RegistrationRequest;
import si.ferbisek.ride_journal.dto.response.LoginResponse;
import si.ferbisek.ride_journal.dto.response.RegistrationResponse;
import si.ferbisek.ride_journal.entity.User;
import si.ferbisek.ride_journal.security.JwtTokenProvider;
import si.ferbisek.ride_journal.service.AuthService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        User registeredUser = authService.register(registrationRequest.getUsername(), registrationRequest.getPassword());

        RegistrationResponse registrationResponse = new RegistrationResponse();
        registrationResponse.setId(registeredUser.getId());
        registrationResponse.setUsername(registeredUser.getUsername());

        return new ResponseEntity<>(registrationResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authService.authenticate(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        // generate token
        String jwtToken = jwtTokenProvider.generateJwtToken(userDetails);

        // put token in login response
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }


}
