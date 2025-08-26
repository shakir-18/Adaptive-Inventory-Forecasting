package com.Inventory.InventoryManagement.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final String SECRET = "X9a7B3qLmZpT8vR4yC6uN2kD1oF5wG7hJ3sL8mQ2rV6tB9pY4xE7cU1zO5fK0nM2";

    private static final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(UserDetails userDetails) {
        logger.trace("User details received");
        logger.info("Generated JWT Token");
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUserId(String token) {
        logger.trace("JWT Token received");
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        logger.trace("JWT Token && User details received");
        final String username = extractUserId(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        try {
            logger.trace("Claims returned");
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims getClaims(String token) {
        logger.info("JWT Token received");
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    public long getRemainingValidity(String token) {
        logger.info("JWT Token received");
        try {
            Claims claims = getClaims(token);
            Date expiration = claims.getExpiration();
            long now = System.currentTimeMillis();
            return Math.max(0, (expiration.getTime() - now) / 1000);
        } catch (ExpiredJwtException e) {
            return 0;
        }
    }
}