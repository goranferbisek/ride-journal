package si.ferbisek.ride_journal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {

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

    @Column(nullable = true)
    private int year;

    @Column(name = "license_plate", length = 20, nullable = true)
    private String licensePlate;

    @Column(name = "vin", length = 17, nullable = true)
    private String vinNumber;

    //TODO cratedAt, updatedAt
}
