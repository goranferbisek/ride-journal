package si.ferbisek.ride_journal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table( name = "users")
public class User extends BaseEntity {

    @Column(length = 50, nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String passwordHash;
}
