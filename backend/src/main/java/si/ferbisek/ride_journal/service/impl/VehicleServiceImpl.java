package si.ferbisek.ride_journal.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
}
