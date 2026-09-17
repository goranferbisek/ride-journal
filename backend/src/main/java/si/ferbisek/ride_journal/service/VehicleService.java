package si.ferbisek.ride_journal.service;

import si.ferbisek.ride_journal.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicle> getAllForUser(Long id);
    Optional<Vehicle> getByIdForUser(Long vehicleId, Long userId);
    Vehicle create(Vehicle vehicle);
    Vehicle update(Long id, Vehicle vehicle);
    void delete(Long id);
}
