package si.ferbisek.ride_journal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class Vehicle extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private User userId; // TODO make this a foreign key

    @Column(length = 100, nullable = false)
    private String brand;

    @Column(length = 100, nullable = false)
    private String model;

    // enum type (CAR, MOTORCYCLE, MOTORHOME, OTHER) // TODO turn this enum

    @Column(nullable = true)
    private int year;

    @Column(name = "license_plate",length = 20, nullable = true)
    private String licensePlate;

    @Column(name="vin", length = 17, nullable = true)
    private String vinNumber;

    //TODO cratedAt, updatedAt
}
