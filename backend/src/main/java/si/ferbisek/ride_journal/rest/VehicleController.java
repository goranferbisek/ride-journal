package si.ferbisek.ride_journal.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import si.ferbisek.ride_journal.dto.VehicleDto;
import si.ferbisek.ride_journal.dto.request.VehicleRequest;
import si.ferbisek.ride_journal.entity.User;
import si.ferbisek.ride_journal.entity.Vehicle;
import si.ferbisek.ride_journal.exception.ResourceNotFoundException;
import si.ferbisek.ride_journal.mapper.VehicleMapper;
import si.ferbisek.ride_journal.security.CustomUserDetails;
import si.ferbisek.ride_journal.service.VehicleService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;

    @GetMapping
    public ResponseEntity<List<VehicleDto>> getAllVehiclesForUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<Vehicle> usersVehicles = vehicleService.getAllForUser(currentUser.getId());
        return ResponseEntity.ok(vehicleMapper.toResponses(usersVehicles));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<VehicleDto> getUsersVehicleById(@PathVariable Long id,
                                                          @AuthenticationPrincipal CustomUserDetails currentUser) {
        Vehicle vehicle = vehicleService.getByIdForUser(id, currentUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vehicle with id = " + id + " not found")
                );

        return ResponseEntity.ok(vehicleMapper.toResponse(vehicle));
    }

    @PostMapping
    public ResponseEntity<VehicleDto> crateNewVehicle(@Valid @RequestBody VehicleRequest vehicleRequest,
                                                      @AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = new User();
        user.setId(currentUser.getId());
        Vehicle newVehicle = vehicleMapper.toEntity(vehicleRequest, user);

        Vehicle cratedVehicle = vehicleService.create(newVehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleMapper.toResponse(cratedVehicle));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<VehicleDto> fullUpdate(@PathVariable Long id,
                                                 @Valid @RequestBody VehicleRequest vehicleRequest,
                                                 @AuthenticationPrincipal CustomUserDetails currentUser) {
        Vehicle vehicle = vehicleService.getByIdForUser(id, currentUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vehicle with id = " + id + " not found")
                );

        vehicleMapper.updateEntity(vehicleRequest, vehicle);
        Vehicle updatedVehicle = vehicleService.update(id, vehicle);

        return ResponseEntity.ok(vehicleMapper.toResponse(updatedVehicle));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
        vehicleService.deleteByIdForUser(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
