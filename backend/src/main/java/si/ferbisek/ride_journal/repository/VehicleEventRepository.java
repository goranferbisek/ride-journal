package si.ferbisek.ride_journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import si.ferbisek.ride_journal.entity.VehicleEvent;

import java.util.List;

@Repository
public interface VehicleEventRepository extends JpaRepository<VehicleEvent, Long> {

    List<VehicleEvent> findAllByVehicle_IdOrderByDateAsc(Long vehicleId);
}
