package si.ferbisek.ride_journal.service.impl;

import org.junit.jupiter.api.Test;
import si.ferbisek.ride_journal.exception.BadRequestException;
import si.ferbisek.ride_journal.repository.VehicleEventRepository;
import si.ferbisek.ride_journal.repository.VehicleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

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
}
