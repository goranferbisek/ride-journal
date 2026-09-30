package si.ferbisek.ride_journal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "vehicle_events",
        indexes = {
                @Index(name = "ix_vehicle_events_vehicle_id_event_date", columnList = "vehicle_id, event_date"),
                @Index(name = "ix_vehicle_events_vehicle_id_event_type", columnList = "vehicle_id, event_type")
        },
        check = @CheckConstraint(name = "ck_vehicle_events_odometer_km", constraint = "odometer_km >= 0")
)
public class VehicleEvent extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Vehicle vehicle;

    @Column(name = "event_date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private EventType eventType;

    @Column(name = "odometer_km")
    private Integer odometerKm;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
