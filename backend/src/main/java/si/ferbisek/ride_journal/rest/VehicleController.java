package si.ferbisek.ride_journal.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicle")
public class VehicleController {

    // Temporary endpoint to test a non-public path
    @GetMapping
    public ResponseEntity<String> testAuthorization() {
        return new ResponseEntity<>("I guess your JWT token is ok", HttpStatus.OK);
    }
}
