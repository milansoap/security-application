package com.example.websecurity.controllers;

import com.example.websecurity.config.JWTUtill;
import com.example.websecurity.dto.AuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.parameters.P;
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

    @PostMapping("/authenticate")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> authenticate(@RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        final UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        if (user != null) {
            return ResponseEntity.ok(jwtUtill.generateToken(user));
        }
        else {
            return ResponseEntity.status(400).body("Some error has occured");
        }

    }

    @PostMapping("/validateToken")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Boolean> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String email = request.get("email");

        UserDetails userDetails = null;
        try {
            userDetails = userDetailsService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
        }

        boolean isValid = userDetails != null && jwtUtill.validateToken(token, userDetails);
        return ResponseEntity.ok(isValid);
    }

}
