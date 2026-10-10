package si.ferbisek.ride_journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import si.ferbisek.ride_journal.entity.VehicleEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleEventRepository extends JpaRepository<VehicleEvent, Long>, JpaSpecificationExecutor<VehicleEvent> {

    List<VehicleEvent> findAllByVehicle_IdOrderByDateAsc(Long vehicleId);

    Optional<VehicleEvent> findByIdAndVehicle_Id(Long id, Long vehicleId);

    Optional<VehicleEvent> findFirstByVehicle_IdAndOdometerKmIsNotNullAndDateLessThanEqualAndIdNotOrderByDateDescIdDesc(
            Long vehicleId, LocalDate date, Long excludedId);
}
