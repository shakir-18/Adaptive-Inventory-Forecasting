package com.Inventory.InventoryManagement;

import com.Inventory.InventoryManagement.utility.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        userDetails = User.withUsername("1")
                .password("password")
                .roles("EMPLOYEE")
                .build();
    }

    @Test
    void generateAndValidateToken()
    {
        String token= jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        String extractedUserId = jwtUtil.extractUserId(token);
        assertEquals("1", extractedUserId);
        assertTrue(jwtUtil.validateToken(token,userDetails));
    }
    @Test
    void tokenShouldExpire() throws InterruptedException {
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1500))
                .signWith(Keys.hmacShaKeyFor("X9a7B3qLmZpT8vR4yC6uN2kD1oF5wG7hJ3sL8mQ2rV6tB9pY4xE7cU1zO5fK0nM2".getBytes()), SignatureAlgorithm.HS512)
                .compact();

        assertTrue(jwtUtil.validateToken(token, userDetails));
        Thread.sleep(2500);
        assertFalse(jwtUtil.validateToken(token, userDetails));
    }
}