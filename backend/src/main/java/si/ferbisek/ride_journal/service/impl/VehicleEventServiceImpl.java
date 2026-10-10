package si.ferbisek.ride_journal.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import si.ferbisek.ride_journal.entity.EventType;
import si.ferbisek.ride_journal.entity.Vehicle;
import si.ferbisek.ride_journal.entity.VehicleEvent;
import si.ferbisek.ride_journal.exception.ResourceNotFoundException;
import si.ferbisek.ride_journal.repository.VehicleEventRepository;
import si.ferbisek.ride_journal.repository.VehicleRepository;
import si.ferbisek.ride_journal.service.VehicleEventService;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Transactional
public class VehicleEventServiceImpl implements VehicleEventService {

    static final String ODOMETER_REGRESSION_WARNING = "odometer lower than previous reading";
    private static final long NO_EVENT = -1L;

    private final VehicleEventRepository eventRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleEvent> getAll(Long userId, Long vehicleId, EventType eventType, Integer fromYear, Integer toYear, Pageable pageable) {
        requireVehicle(userId, vehicleId);

        Specification<VehicleEvent> spec = (root, query, cb) -> cb.equal(root.get("vehicle").get("id"), vehicleId);
        if (eventType != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("eventType"), eventType));
        }
        if (fromYear != null) {
            LocalDate from = LocalDate.of(fromYear, 1, 1);
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.<LocalDate>get("date"), from));
        }
        if (toYear != null) {
            LocalDate to = LocalDate.of(toYear, 12, 31);
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.<LocalDate>get("date"), to));
        }
        return eventRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleEvent getById(Long userId, Long vehicleId, Long eventId) {
        requireVehicle(userId, vehicleId);
        return requireEvent(vehicleId, eventId);
    }

    @Override
    public SavedEvent create(Long userId, Long vehicleId, VehicleEvent event) {
        Vehicle vehicle = requireVehicle(userId, vehicleId);
        event.setVehicle(vehicle);
        String warning = checkOdometer(vehicleId, event, NO_EVENT);
        return new SavedEvent(eventRepository.save(event), warning);
    }

    @Override
    public SavedEvent update(Long userId, Long vehicleId, Long eventId, VehicleEvent changes) {
        requireVehicle(userId, vehicleId);
        VehicleEvent event = requireEvent(vehicleId, eventId);
        event.setDate(changes.getDate());
        event.setEventType(changes.getEventType());
        event.setOdometerKm(changes.getOdometerKm());
        event.setNotes(changes.getNotes());
        String warning = checkOdometer(vehicleId, event, eventId);
        return new SavedEvent(eventRepository.save(event), warning);
    }

    @Override
    public void delete(Long userId, Long vehicleId, Long eventId) {
        requireVehicle(userId, vehicleId);
        eventRepository.delete(requireEvent(vehicleId, eventId));
    }

    private Vehicle requireVehicle(Long userId, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.getByIdAndUser_Id(vehicleId, userId);
        if (vehicle == null) {
            throw new ResourceNotFoundException("Vehicle with id = " + vehicleId + " not found");
        }
        return vehicle;
    }

    private VehicleEvent requireEvent(Long vehicleId, Long eventId) {
        return eventRepository.findByIdAndVehicle_Id(eventId, vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id = " + eventId + " not found"));
    }

    private String checkOdometer(Long vehicleId, VehicleEvent event, Long excludedEventId) {
        if (event.getOdometerKm() == null) {
            return null;
        }
        return eventRepository
                .findFirstByVehicle_IdAndOdometerKmIsNotNullAndDateLessThanEqualAndIdNotOrderByDateDescIdDesc(
                        vehicleId, event.getDate(), excludedEventId)
                .filter(previous -> event.getOdometerKm() < previous.getOdometerKm())
                .map(previous -> ODOMETER_REGRESSION_WARNING)
                .orElse(null);
    }
}
