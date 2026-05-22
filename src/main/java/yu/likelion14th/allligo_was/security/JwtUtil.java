package yu.likelion14th.allligo_was.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKeySpec secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String issuer;

    public JwtUtil(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration.access-token}") long accessTokenExpiration,
            @Value("${jwt.expiration.refresh-token}") long refreshTokenExpiration,
            @Value("${jwt.issuer}") String issuer
    ) {
        this.secretKey = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.issuer = issuer;
    }

    public String generateAccessToken(Long userId, String email) {
        return generateToken(userId, email, accessTokenExpiration);
    }

    public String generateRefreshToken(Long userId, String email) {
        return generateToken(userId, email, refreshTokenExpiration);
    }

    private String generateToken(Long userId, String email, long expiration) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public Long parseUserId(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}