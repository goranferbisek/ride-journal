package si.ferbisek.ride_journal.dto.response;

import si.ferbisek.ride_journal.entity.VehicleType;

public record VehicleResponse(
        Long id,
        String brand,
        String model,
        VehicleType type,
        Integer year,
        String licensePlate,
        String vinNumber
) {}
