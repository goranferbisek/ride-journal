package si.ferbisek.ride_journal.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import si.ferbisek.ride_journal.dto.EventDto;
import si.ferbisek.ride_journal.dto.request.EventRequest;
import si.ferbisek.ride_journal.entity.EventType;
import si.ferbisek.ride_journal.mapper.VehicleEventMapper;
import si.ferbisek.ride_journal.security.CustomUserDetails;
import si.ferbisek.ride_journal.service.VehicleEventService;
import si.ferbisek.ride_journal.service.VehicleEventService.SavedEvent;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/vehicles/{vehicleId}/events")
public class VehicleEventController {

    private static final int MAX_PAGE_SIZE = 100;

    private final VehicleEventService eventService;
    private final VehicleEventMapper eventMapper;

    @GetMapping
    public ResponseEntity<Page<EventDto>> getAll(@PathVariable Long vehicleId,
                                                 @RequestParam(required = false) EventType eventType,
                                                 @RequestParam(required = false) Integer fromYear,
                                                 @RequestParam(required = false) Integer toYear,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @AuthenticationPrincipal CustomUserDetails currentUser) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "id")));
        return ResponseEntity.ok(eventService
                .getAll(currentUser.getId(), vehicleId, eventType, fromYear, toYear, pageable)
                .map(eventMapper::toResponse));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getById(@PathVariable Long vehicleId,
                                            @PathVariable Long eventId,
                                            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(eventMapper.toResponse(eventService.getById(currentUser.getId(), vehicleId, eventId)));
    }

    @PostMapping
    public ResponseEntity<EventDto> create(@PathVariable Long vehicleId,
                                           @Valid @RequestBody EventRequest request,
                                           @AuthenticationPrincipal CustomUserDetails currentUser) {
        SavedEvent saved = eventService.create(currentUser.getId(), vehicleId, eventMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toResponse(saved.event(), saved.warning()));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> update(@PathVariable Long vehicleId,
                                           @PathVariable Long eventId,
                                           @Valid @RequestBody EventRequest request,
                                           @AuthenticationPrincipal CustomUserDetails currentUser) {
        SavedEvent saved = eventService.update(currentUser.getId(), vehicleId, eventId, eventMapper.toEntity(request));
        return ResponseEntity.ok(eventMapper.toResponse(saved.event(), saved.warning()));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable Long vehicleId,
                                       @PathVariable Long eventId,
                                       @AuthenticationPrincipal CustomUserDetails currentUser) {
        eventService.delete(currentUser.getId(), vehicleId, eventId);
        return ResponseEntity.noContent().build();
    }
}
