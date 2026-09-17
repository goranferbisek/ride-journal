package si.ferbisek.ride_journal.service;

import si.ferbisek.ride_journal.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    List<Vehicle> getAllForUser(Long id);
    Vehicle getByIdForUser(Long vehicleId, Long userId);
}
