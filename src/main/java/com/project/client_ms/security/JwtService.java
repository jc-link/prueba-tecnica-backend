package com.project.client_ms.security;


import com.project.client_ms.entities.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.logging.Logger;

@Service
public class JwtService {

    private static final Logger LOGGER = Logger.getLogger(JwtService.class.getName());

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;


    public String generateToken(final AppUser appUser) {
        return buildToken(appUser, jwtExpiration);
    }

    public String generateRefreshToken(final AppUser appUser) {
        return buildToken(appUser, refreshExpiration);
    }

    public String extractUsername(String token) {
        Claims jwtToken = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return jwtToken.getSubject();
    }

    private String buildToken(final AppUser appUser, final long expirationTime) {
        LOGGER.info("Creating token for user: " + appUser.getUsername());
        return Jwts.builder()
                .setSubject(appUser.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, AppUser appUser) {
        String username = extractUsername(token);
        return (username.equals(appUser.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        Claims jwtToken = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return jwtToken.getExpiration();
    }


}
