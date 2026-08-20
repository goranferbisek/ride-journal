package si.ferbisek.ride_journal.service;

import org.springframework.security.core.userdetails.UserDetails;
import si.ferbisek.ride_journal.entity.User;

public interface AuthService {
    User register(String username, String password);
    UserDetails authenticate (String username, String password);
}
