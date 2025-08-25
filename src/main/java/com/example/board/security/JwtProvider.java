package com.example.board.security;

import com.example.board.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtProvider {

    private final Key key;
    private final long accessTtl;
    private final long refreshTtl;
    private boolean liked;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.access-ttl-seconds}") long accessTtl,
                       @Value("${jwt.refresh-ttl-seconds}") long refreshTtl) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtl = accessTtl;
        this.refreshTtl = refreshTtl;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .addClaims(Map.of("role", user.getRole().name()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTtl)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTtl)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public long getRemainingSeconds(String token) {
        var claims = parse(token).getBody();
        long remainMs = claims.getExpiration().getTime() - System.currentTimeMillis();
        return Math.max(0, remainMs / 1000);
    }
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        Long userId = Long.valueOf(claims.getSubject());
        String role = claims.get("role", String.class);
        var authorities = (role == null)
                ? List.<GrantedAuthority>of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + role));
        var principal = new UserPrincipal(userId, role);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

}
