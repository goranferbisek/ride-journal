package si.ferbisek.ride_journal.rest;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import si.ferbisek.ride_journal.dto.response.VehicleResponse;
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
    public ResponseEntity<VehicleResponse> getUsersVehicleById(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails currentUser) {
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
}
