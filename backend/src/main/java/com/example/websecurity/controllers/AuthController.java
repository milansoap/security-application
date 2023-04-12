package com.example.websecurity.controllers;

import com.example.websecurity.config.JWTUtill;
import com.example.websecurity.dao.UserDao;
import com.example.websecurity.dto.AuthenticationRequest;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JWTUtill jwtUtill;
    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;
    Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();



    @PostMapping("/authenticate")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
        if(!bucket.tryConsume(1)){
            return ResponseEntity.status(429).body("Rate limit exceeded");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            final UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
            System.out.println(user.toString());

            if (user != null) {
                resetUserFailedAttempts(request.getEmail());
                return ResponseEntity.ok(jwtUtill.generateToken(user));
            } else {
                return ResponseEntity.status(400).body("Some error has occurred");
            }

        } catch (AuthenticationException e) {
            incrementUserFailedAttempts(request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error has occurred");
        }

    }

    private void updateUserFailedAttempts(String email, boolean increment) {
        String sql = increment ? "UPDATE user SET failed_attempt = failed_attempt + 1 WHERE email = ?" : "UPDATE user SET failed_attempt = 0 WHERE email = ?";
        jdbcTemplate.update(sql, email);
    }

    private void resetUserFailedAttempts(String email) {
        updateUserFailedAttempts(email, false);
    }

    private void incrementUserFailedAttempts(String email) {
        updateUserFailedAttempts(email, true);
    }

    @PostMapping("/validateAdmin")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Boolean> validateAdmin(@RequestBody Map<String, String> request) {
        if(!bucket.tryConsume(1)){
            return ResponseEntity.status(429).body(false);
        }
        String token = request.get("token");

        boolean isValid = jwtUtill.validateTokenAdmin(token);
        if (isValid) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    @PostMapping("/validateToken")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Boolean> validateToken(@RequestBody Map<String, String> request) {
        if(!bucket.tryConsume(1)){
            return ResponseEntity.status(429).body(false);
        }
        String token = request.get("token");

        boolean isValid = jwtUtill.validateTokenUser(token);
        if (isValid) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }

    @PostMapping("/submitForm")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> submitForm(@RequestBody String formData, @RequestHeader("Authorization") String token) {
        if(!bucket.tryConsume(1)){
            return ResponseEntity.status(429).body("Rate limit exceeded");
        }
        boolean isValid = jwtUtill.validateTokenUser(token);

        if (isValid) {
            System.out.println(formData + " - Success");
            return ResponseEntity.ok("Form submitted successfully");
        } else {
            System.out.println(formData + " - Token not ok");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    @PostMapping("/submitFormAdmin")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<?> submitFormAdmin(@RequestBody String formData, @RequestHeader("Authorization") String token) {
        if(!bucket.tryConsume(1)){
            return ResponseEntity.status(429).body("Rate limit exceeded");
        }
        boolean isValid = jwtUtill.validateTokenAdmin(token);

        if (isValid) {
            System.out.println(formData + " - Success Admin Form");
            return ResponseEntity.ok("Form submitted successfully");
        } else {
            System.out.println(formData + " - Admin Token Not Ok");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }
}
