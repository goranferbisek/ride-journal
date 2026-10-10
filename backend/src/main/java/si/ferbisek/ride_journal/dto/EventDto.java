package si.ferbisek.ride_journal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import si.ferbisek.ride_journal.entity.EventType;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventDto(
        Long id,
        LocalDate date,
        EventType eventType,
        Integer odometerKm,
        String notes,
        String warning
) {}
