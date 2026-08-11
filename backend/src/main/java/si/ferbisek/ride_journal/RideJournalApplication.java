package si.ferbisek.ride_journal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RideJournalApplication {

	public static void main(String[] args) {
		SpringApplication.run(RideJournalApplication.class, args);
	}

}
