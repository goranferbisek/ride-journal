package si.ferbisek.ride_journal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import si.ferbisek.ride_journal.entity.EventType;
import si.ferbisek.ride_journal.entity.VehicleEvent;

public interface VehicleEventService {

    record SavedEvent(VehicleEvent event, String warning) {}

    Page<VehicleEvent> getAll(Long userId, Long vehicleId, EventType eventType, Integer fromYear, Integer toYear, Pageable pageable);

    VehicleEvent getById(Long userId, Long vehicleId, Long eventId);

    SavedEvent create(Long userId, Long vehicleId, VehicleEvent event);

    SavedEvent update(Long userId, Long vehicleId, Long eventId, VehicleEvent changes);

    void delete(Long userId, Long vehicleId, Long eventId);
}
