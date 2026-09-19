package si.ferbisek.ride_journal.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import si.ferbisek.ride_journal.dto.request.VehicleRequest;
import si.ferbisek.ride_journal.dto.response.VehicleResponse;
import si.ferbisek.ride_journal.entity.User;
import si.ferbisek.ride_journal.entity.Vehicle;
import si.ferbisek.ride_journal.exception.ResourceNotFoundException;
import si.ferbisek.ride_journal.mapper.VehicleMapper;
import si.ferbisek.ride_journal.security.CustomUserDetails;
import si.ferbisek.ride_journal.service.VehicleService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehiclesForUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<Vehicle> usersVehicles = vehicleService.getAllForUser(currentUser.getId());
        return ResponseEntity.ok(vehicleMapper.toResponses(usersVehicles));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<VehicleResponse> getUsersVehicleById(@PathVariable Long id,
                                                               @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Vehicle> vehicleOptional = vehicleService.getByIdForUser(id, currentUser.getId());

        if (vehicleOptional.isEmpty()) {
            throw new ResourceNotFoundException("Vehicle with id = " + id + " not found");
        }

        return ResponseEntity.ok(vehicleMapper.toResponse(vehicleOptional.get()));
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> crateNewVehicle(@Valid @RequestBody VehicleRequest vehicleRequest,
                                                           @AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = new User();
        user.setId(currentUser.getId());
        Vehicle newVehicle = vehicleMapper.toEntity(vehicleRequest, user);

        Vehicle cratedVehicle = vehicleService.create(newVehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleMapper.toResponse(cratedVehicle));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<VehicleResponse> fullUpdate(@PathVariable Long id,
                                                      @Valid @RequestBody VehicleRequest vehicleRequest,
                                                      @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Vehicle> existingVehicleOptional = vehicleService.getByIdForUser(id, currentUser.getId());
        if (existingVehicleOptional.isEmpty()) {
            throw new ResourceNotFoundException("Vehicle with id = " + id + " not found");
        }

        User user = new User();
        user.setId(currentUser.getId());
        Vehicle newVehicle = vehicleMapper.toEntity(vehicleRequest, user);

        Vehicle updatedVehicle = vehicleService.update(id, newVehicle);

        return ResponseEntity.ok(vehicleMapper.toResponse(updatedVehicle));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        vehicleService.deleteByIdForUser(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
