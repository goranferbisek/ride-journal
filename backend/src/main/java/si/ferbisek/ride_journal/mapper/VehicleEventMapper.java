package si.ferbisek.ride_journal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import si.ferbisek.ride_journal.dto.EventDto;
import si.ferbisek.ride_journal.dto.request.EventRequest;
import si.ferbisek.ride_journal.entity.VehicleEvent;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface VehicleEventMapper {

    @Mapping(target = "warning", ignore = true)
    EventDto toResponse(VehicleEvent event);

    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "date", source = "event.date")
    @Mapping(target = "eventType", source = "event.eventType")
    @Mapping(target = "odometerKm", source = "event.odometerKm")
    @Mapping(target = "notes", source = "event.notes")
    @Mapping(target = "warning", source = "warning")
    EventDto toResponse(VehicleEvent event, String warning);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    VehicleEvent toEntity(EventRequest request);
}
