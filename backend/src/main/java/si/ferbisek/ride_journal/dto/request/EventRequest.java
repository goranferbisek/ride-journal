package si.ferbisek.ride_journal.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import si.ferbisek.ride_journal.entity.EventType;
import si.ferbisek.ride_journal.validation.NotFutureDate;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotNull(message = "Date is required")
    @NotFutureDate
    private LocalDate date;

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @Min(value = 0, message = "Odometer must be >= 0")
    private Integer odometerKm;

    private String notes;
}
