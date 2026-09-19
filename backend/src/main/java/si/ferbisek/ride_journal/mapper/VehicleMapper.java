package si.ferbisek.ride_journal.mapper;

import org.mapstruct.*;
import si.ferbisek.ride_journal.dto.request.VehicleRequest;
import si.ferbisek.ride_journal.dto.VehicleDto;
import si.ferbisek.ride_journal.entity.User;
import si.ferbisek.ride_journal.entity.Vehicle;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface VehicleMapper {

    VehicleDto toResponse(Vehicle vehicle);

    List<VehicleDto> toResponses(List<Vehicle> vehicles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    Vehicle toEntity(VehicleRequest vehicleRequest, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntity(VehicleRequest request, @MappingTarget Vehicle vehicle);

}
