package si.ferbisek.ride_journal.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import si.ferbisek.ride_journal.entity.Vehicle;
import si.ferbisek.ride_journal.repository.VehicleRepository;
import si.ferbisek.ride_journal.service.VehicleService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> getAllForUser(Long id) {
        return vehicleRepository.findAllByUser_Id(id);
    }

    @Override
    public Optional<Vehicle> getByIdForUser(Long vehicleId, Long userId) {
        return Optional.ofNullable(vehicleRepository.getByIdAndUser_Id(vehicleId, userId));
    }

    @Override
    public Vehicle create(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle update(Long id, Vehicle vehicle) {
        vehicle.setId(id);
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void deleteByIdForUser(Long vehicleId, Long userId) {
        vehicleRepository.deleteByIdAndUser_Id(vehicleId, userId);
    }
}
