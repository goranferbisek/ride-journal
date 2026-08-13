package si.ferbisek.ride_journal.service;

import si.ferbisek.ride_journal.entity.User;

public interface AuthService {

    User register(String username, String password);
}
