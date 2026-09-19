package si.ferbisek.ride_journal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "vehicles",
        indexes = {
                @Index(name="ix_vehicles_user_id", columnList = "user_id")
        }
)
public class Vehicle extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100, nullable = false)
    private String brand;

    @Column(length = 100, nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private VehicleType type;

    private Integer year;

    @Column(name = "license_plate", length = 20)
    private String licensePlate;

    @Column(name = "vin", length = 17)
    private String vin;

}
