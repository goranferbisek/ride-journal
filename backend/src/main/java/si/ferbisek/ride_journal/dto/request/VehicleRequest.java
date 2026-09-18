package si.ferbisek.ride_journal.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import si.ferbisek.ride_journal.entity.VehicleType;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {

    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand must be between {min} and {max} characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(min = 3, max = 50, message = "Model must be between {min} and {max} characters")
    private String model;

    @NotNull(message = "Vehicle type is mandatory")
    private VehicleType type;

    @Min(1900)
    @Max(2026)
    private Integer year;

    @Size(max = 20, message = "License plate number is {max} characters max")
    private String licensePlate;

    @Size(min = 17, max = 17, message = "VIN number must be exactly 17 characters")
    private String vinNumber;

}
