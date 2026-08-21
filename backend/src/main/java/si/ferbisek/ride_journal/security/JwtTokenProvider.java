package si.ferbisek.ride_journal.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiry}")
    private long jwtExpiryMillis;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public TokenWithExpiresAt generateJwtToken(UserDetails userDetails) {
        long now = System.currentTimeMillis();

        String jwtToken = Jwts.builder()
                .issuer("ride-journal")
                .subject(userDetails.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpiryMillis))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        return new TokenWithExpiresAt(jwtToken, jwtExpiryMillis);
    }

    //TODO: implement method to validate token

}
