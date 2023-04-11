package com.example.websecurity.controllers;

import com.example.websecurity.config.JWTUtill;
import com.example.websecurity.dao.UserDao;
import com.example.websecurity.dto.AuthenticationRequest;
import com.example.websecurity.exceptions.LoginWayException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountLockedException;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/auth")
public class OAuthController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JWTUtill jwtUtill;

    public ResponseEntity<String> generateJwtToken(String email) {

        try {
            UserDetails user = userDao.findUserByEmail(email, "oauthLogin");
            System.out.println(user.toString());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            String jwt = jwtUtill.generateToken(user);
            return ResponseEntity.ok(jwt);
        } catch (SQLException | AccountLockedException | LoginWayException e) {
            throw new RuntimeException(e);
        }

    }
}

