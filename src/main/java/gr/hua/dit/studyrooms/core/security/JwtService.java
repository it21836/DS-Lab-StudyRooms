package gr.hua.dit.studyrooms.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

@Service
public class JwtService {

    private Key key;
    private String issuer;
    private String audience;
    private long ttl;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.issuer}") String issuer,
                      @Value("${app.jwt.audience}") String audience,
                      @Value("${app.jwt.ttl-minutes}") long ttl) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.ttl = ttl;
    }

    public String issue(String subject, Collection<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(subject)
            .issuer(issuer)
            .setAudience(audience)
            .claim("roles", roles)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(Duration.ofMinutes(ttl))))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
            .requireAudience(audience)
            .requireIssuer(issuer)
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
