package com.example.websecurity.controllers;

import com.example.websecurity.config.JWTUtill;
import com.example.websecurity.dao.UserDao;
import com.example.websecurity.dto.AuthenticationRequest;
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



    @PostMapping("/authenticate")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
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


//    @PostMapping("/validateToken")
//    @CrossOrigin(origins = "http://localhost:3000")
//    public ResponseEntity<Boolean> validateToken(@RequestBody Map<String, String> request) {
//        String token = request.get("token");
//        String email = request.get("email");
//
//        UserDetails userDetails = null;
//        try {
//            user = userDao.findUserByEmail(email);
//        } catch (UsernameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        boolean isValid = userDetails != null && jwtUtill.validateToken(token, userDetails);
//        return ResponseEntity.ok(isValid);
//    }

}
