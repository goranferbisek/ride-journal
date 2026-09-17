package si.ferbisek.ride_journal.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import si.ferbisek.ride_journal.entity.Vehicle;
import si.ferbisek.ride_journal.repository.VehicleRepository;
import si.ferbisek.ride_journal.service.VehicleService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> getAllForUser(Long id) {
        return vehicleRepository.findAllByUser_Id(id);
    }

    @Override
    public Vehicle getByIdForUser(Long vehicleId, Long userId) {
        return vehicleRepository.getByIdAndUser_Id(vehicleId, userId);
    }
}
