package si.ferbisek.ride_journal.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import si.ferbisek.ride_journal.entity.User;
import si.ferbisek.ride_journal.exception.UsernameAlreadyExistsException;
import si.ferbisek.ride_journal.repository.UserRepository;
import si.ferbisek.ride_journal.service.AuthService;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public User register(String username, String hashedPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashedPassword);

        User newUser = userRepository.save(user);

        return newUser;
    }
}
