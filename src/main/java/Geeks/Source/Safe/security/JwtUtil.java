package Geeks.Source.Safe.security;

import Geeks.Source.Safe.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long JWT_EXPIRATION = 3600000; // 1 hour
    private static final long JWT_REFRESH_EXPIRATION = 86400000; // 24 hours

    private Key getSigningKey() {
        // Decode the Base64 encoded secret key
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserName())
                .setIssuedAt(new Date())
                .claim("roles", List.of(user.getRole().name()))  // Store roles as a list
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserName())
                .setIssuedAt(new Date())
                .claim("roles", List.of(user.getRole().name()))  // Store roles as a list
                .setExpiration(new Date(System.currentTimeMillis() + JWT_REFRESH_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration time
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract Claims from Token
    private <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    // Extract all claims from Token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Use decoded secret key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if Token is expired
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate Token
    public boolean isTokenValid(String token) {
        final String username = extractUsername(token);
        return (!isTokenExpired(token));
    }

    // Interface for extracting claim
    @FunctionalInterface
    private interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }

    // Extract roles as List<String>
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        // Get roles as List from token, assuming roles are stored as a list
        return claims.get("roles", List.class);
    }

}
