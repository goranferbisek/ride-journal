package si.ferbisek.ride_journal.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import si.ferbisek.ride_journal.entity.VehicleEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleEventRepository extends JpaRepository<VehicleEvent, Long>, JpaSpecificationExecutor<VehicleEvent> {

    List<VehicleEvent> findAllByVehicle_IdOrderByDateAsc(Long vehicleId);

    Optional<VehicleEvent> findByIdAndVehicle_Id(Long id, Long vehicleId);

    @Query("""
            SELECT event FROM VehicleEvent event
            WHERE event.vehicle.id = :vehicleId
              AND event.odometerKm IS NOT NULL
              AND (event.date < :date OR (event.date = :date AND (:eventId IS NULL OR event.id < :eventId)))
            ORDER BY event.date DESC, event.id DESC
            """)
    List<VehicleEvent> findPreviousOdometerReadings(
            @Param("vehicleId") Long vehicleId,
            @Param("date") LocalDate date,
            @Param("eventId") Long eventId,
            Pageable pageable);
}
