package si.ferbisek.ride_journal.security;

public record TokenWithExpiresAt(String jwtToken, long expiresAt) {
}
