package si.ferbisek.ride_journal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import si.ferbisek.ride_journal.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
