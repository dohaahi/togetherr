package together.together_project.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    public String generateToken(Long userId) {
        Instant now = Instant.now();

        SecretKey secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64URL.decode("o4OdCNjd8mmDN2+/nfHdIB2ZWta80foXqDx2rouL4nw="));

        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .claim("userId", userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(7, ChronoUnit.DAYS))) // 토큰 만료 시간(7일로 설정)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


}
