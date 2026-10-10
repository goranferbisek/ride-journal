package si.ferbisek.ride_journal.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import si.ferbisek.ride_journal.entity.Vehicle;
import si.ferbisek.ride_journal.entity.VehicleEvent;
import si.ferbisek.ride_journal.exception.BadRequestException;
import si.ferbisek.ride_journal.repository.VehicleEventRepository;
import si.ferbisek.ride_journal.repository.VehicleRepository;
import si.ferbisek.ride_journal.service.VehicleEventService.SavedEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class VehicleEventServiceImplTest {

    private final VehicleEventRepository eventRepository = mock(VehicleEventRepository.class);
    private final VehicleRepository vehicleRepository = mock(VehicleRepository.class);
    private final VehicleEventServiceImpl service = new VehicleEventServiceImpl(eventRepository, vehicleRepository);

    @Test
    void rejectsFromYearBelowLocalDateRange() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.getAll(1L, 1L, null, Integer.MIN_VALUE, null, null));

        assertEquals("fromYear must be between -999999999 and 999999999", exception.getMessage());
        verifyNoInteractions(eventRepository, vehicleRepository);
    }

    @Test
    void rejectsToYearAboveLocalDateRange() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> service.getAll(1L, 1L, null, null, Integer.MAX_VALUE, null));

        assertEquals("toYear must be between -999999999 and 999999999", exception.getMessage());
        verifyNoInteractions(eventRepository, vehicleRepository);
    }

    @Test
    void updateUsesOnlyEventsEarlierInDateAndIdOrdering() {
        LocalDate date = LocalDate.of(2026, 10, 1);
        Vehicle vehicle = new Vehicle();
        VehicleEvent event = new VehicleEvent();
        event.setDate(date);
        event.setOdometerKm(100);
        VehicleEvent changes = new VehicleEvent();
        changes.setDate(date);
        changes.setOdometerKm(150);
        VehicleEvent earlierReading = new VehicleEvent();
        earlierReading.setOdometerKm(100);

        when(vehicleRepository.getByIdAndUser_Id(1L, 2L)).thenReturn(vehicle);
        when(eventRepository.findByIdAndVehicle_Id(10L, 1L)).thenReturn(Optional.of(event));
        when(eventRepository.findPreviousOdometerReadings(1L, date, 10L, PageRequest.of(0, 1)))
                .thenReturn(List.of(earlierReading));
        when(eventRepository.save(event)).thenReturn(event);

        SavedEvent saved = service.update(2L, 1L, 10L, changes);

        assertNull(saved.warning());
        verify(eventRepository).findPreviousOdometerReadings(1L, date, 10L, PageRequest.of(0, 1));
    }

    @Test
    void createKeepsSameDayReadingsAsPredecessors() {
        LocalDate date = LocalDate.of(2026, 10, 1);
        Vehicle vehicle = new Vehicle();
        VehicleEvent event = new VehicleEvent();
        event.setDate(date);
        event.setOdometerKm(150);
        VehicleEvent sameDayReading = new VehicleEvent();
        sameDayReading.setOdometerKm(200);

        when(vehicleRepository.getByIdAndUser_Id(1L, 2L)).thenReturn(vehicle);
        when(eventRepository.findPreviousOdometerReadings(1L, date, null, PageRequest.of(0, 1)))
                .thenReturn(List.of(sameDayReading));
        when(eventRepository.save(event)).thenReturn(event);

        SavedEvent saved = service.create(2L, 1L, event);

        assertEquals(VehicleEventServiceImpl.ODOMETER_REGRESSION_WARNING, saved.warning());
        verify(eventRepository).findPreviousOdometerReadings(1L, date, null, PageRequest.of(0, 1));
    }
}
