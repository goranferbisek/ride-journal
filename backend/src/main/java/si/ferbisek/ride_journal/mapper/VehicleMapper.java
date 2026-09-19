package si.ferbisek.ride_journal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import si.ferbisek.ride_journal.dto.request.VehicleRequest;
import si.ferbisek.ride_journal.dto.response.VehicleResponse;
import si.ferbisek.ride_journal.entity.User;
import si.ferbisek.ride_journal.entity.Vehicle;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface VehicleMapper {

    VehicleResponse toResponse(Vehicle vehicle);

    List<VehicleResponse> toResponses(List<Vehicle> vehicles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    Vehicle toEntity(VehicleRequest vehicleRequest, User user);
}
