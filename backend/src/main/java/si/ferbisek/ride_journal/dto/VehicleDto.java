package si.ferbisek.ride_journal.dto;

import si.ferbisek.ride_journal.entity.VehicleType;

import java.time.Instant;

public record VehicleDto(
        Long id,
        String brand,
        String model,
        VehicleType type,
        Integer year,
        String licensePlate,
        String vin,
        Instant createdAt
) {}
