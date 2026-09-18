package si.ferbisek.ride_journal.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import si.ferbisek.ride_journal.entity.VehicleType;

public record VehicleResponse(
        Long id,
        String brand,
        String model,
        VehicleType type,
        Integer year,
        String licensePlate,
        @JsonProperty("vin")
        String vinNumber
) {}
