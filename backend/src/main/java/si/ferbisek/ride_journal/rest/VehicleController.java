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
import si.ferbisek.ride_journal.security.CustomUserDetails;
import si.ferbisek.ride_journal.service.VehicleService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllVehiclesForUser(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<Vehicle> usersVehicles = vehicleService.getAllForUser(currentUser.getId());

        List<VehicleResponse> vehicleResponses = usersVehicles.stream().map(
                vehicle -> new VehicleResponse(
                        vehicle.getId(),
                        vehicle.getBrand(),
                        vehicle.getModel(),
                        vehicle.getType(),
                        vehicle.getYear(),
                        vehicle.getLicensePlate(),
                        vehicle.getVinNumber()
                )
        ).toList();

        return ResponseEntity.ok(vehicleResponses);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<VehicleResponse> getUsersVehicleById(@PathVariable Long id,
                                                               @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Vehicle> vehicleOptional = vehicleService.getByIdForUser(id, currentUser.getId());

        if (vehicleOptional.isPresent()) {
            Vehicle vehicle = vehicleOptional.get();

            VehicleResponse vehicleResponse = new VehicleResponse(
                    vehicle.getId(),
                    vehicle.getBrand(),
                    vehicle.getModel(),
                    vehicle.getType(),
                    vehicle.getYear(),
                    vehicle.getLicensePlate(),
                    vehicle.getVinNumber()
            );
            return ResponseEntity.ok(vehicleResponse);
        } else {
            throw new ResourceNotFoundException("Vehicle with id = " + id + " not found");
        }
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> crateNewVehicle(@Valid @RequestBody VehicleRequest vehicleRequest,
                                                           @AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = new User();
        user.setId(currentUser.getId());

        Vehicle newVehicle = new Vehicle(
                user,
                vehicleRequest.getBrand(),
                vehicleRequest.getModel(),
                vehicleRequest.getType(),
                vehicleRequest.getYear(),
                vehicleRequest.getLicensePlate(),
                vehicleRequest.getVinNumber()
        );

        Vehicle cratedVehicle = vehicleService.create(newVehicle);

        VehicleResponse cratedVehicleResponse = new VehicleResponse(
                cratedVehicle.getId(),
                cratedVehicle.getBrand(),
                cratedVehicle.getModel(),
                cratedVehicle.getType(),
                cratedVehicle.getYear(),
                cratedVehicle.getLicensePlate(),
                cratedVehicle.getVinNumber()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(cratedVehicleResponse);
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

        Vehicle newVehicle = new Vehicle(
                user,
                vehicleRequest.getBrand(),
                vehicleRequest.getModel(),
                vehicleRequest.getType(),
                vehicleRequest.getYear(),
                vehicleRequest.getLicensePlate(),
                vehicleRequest.getVinNumber()
        );

        Vehicle updatedVehicle = vehicleService.update(id, newVehicle);

        VehicleResponse updatedVehicleResponse = new VehicleResponse(
                updatedVehicle.getId(),
                updatedVehicle.getBrand(),
                updatedVehicle.getModel(),
                updatedVehicle.getType(),
                updatedVehicle.getYear(),
                updatedVehicle.getLicensePlate(),
                updatedVehicle.getVinNumber()
        );

        return ResponseEntity.ok(updatedVehicleResponse);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
