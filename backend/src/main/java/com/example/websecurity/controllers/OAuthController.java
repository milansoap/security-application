package com.example.websecurity.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.security.Principal;

@Controller
public class OAuthController {

    @GetMapping("/oauth2-login")
    public String oauth2Login() {
        return "oauth2-login";
    }

    @ResponseBody
    @RequestMapping("user")
    public Principal user(Principal principal) {
        return principal;
    }

}

