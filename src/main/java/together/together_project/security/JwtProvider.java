package together.together_project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import together.together_project.exception.CustomException;
import together.together_project.exception.ErrorCode;

@Component
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;
    public static final String USER_ID_KEY = "userId";
    private static final int AUTH_TOKEN_EXP = 60 * 60 * 1000; // 1Hour in milliseconds

    public String createAccessToken(Long userId) {
        final Claims claims = createClaims(userId);

        return createToken(AUTH_TOKEN_EXP, claims);
    }

    private String createToken(final int tokenExpInMs, final Claims claims) {

        final Date expiresIn = createExpiresIn(tokenExpInMs);
        final Key signingKey = createSigningKey();

        return Jwts.builder().setClaims(claims).setExpiration(expiresIn).signWith(signingKey).compact();
    }

    private Claims createClaims(final Long userId) {
        final Claims claims = Jwts.claims();
        claims.put(USER_ID_KEY, userId);

        return claims;
    }

    private Date createExpiresIn(final int expMillis) {
        final long currentDateTime = new Date().getTime();

        return new Date(currentDateTime + expMillis);
    }

    private Key createSigningKey() {
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public Long verifyAuthTokenOrThrow(final String token) {
        try {
            final Claims claims = parseToClaimsJws(token);
            Date expiration = claims.getExpiration();

            if (expiration.before(new Date())) {
                throw new ExpiredJwtException(null, null, "Expired Token");
            }

            Long userId = Long.valueOf(claims.get(USER_ID_KEY).toString());
            return userId;

            // expired token..
        } catch (final ExpiredJwtException expiredJwtException) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);

            // user made invalid token
        } catch (final RuntimeException runtimeException) {
            throw new CustomException(ErrorCode.TOKEN_VALIDATE);
        }
    }

    public Claims parseToClaimsJws(final String token) {
        final Key key = createSigningKey();

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
