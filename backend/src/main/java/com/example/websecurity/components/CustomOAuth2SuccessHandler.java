package com.example.websecurity.components;

import com.example.websecurity.config.JWTUtill;
import com.example.websecurity.controllers.OAuthController;
import com.example.websecurity.dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {


    private ApplicationContext applicationContext;
    private final JWTUtill jwtUtill;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserDetailsService userDetailsService;
    private final UserDao userDao;
    private final OAuthController oAuthController;

    public CustomOAuth2SuccessHandler(JWTUtill jwtUtill, OAuth2AuthorizedClientService authorizedClientService, UserDetailsService userDetailsService, UserDao userDao, OAuthController oAuthController) {
        this.jwtUtill = jwtUtill;
        this.authorizedClientService = authorizedClientService;
        this.userDetailsService = userDetailsService;
        this.userDao = userDao;
        this.oAuthController = oAuthController;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");


        ResponseEntity<String> tokenResponse = oAuthController.generateJwtToken(email);

        if (tokenResponse.getStatusCode() == HttpStatus.OK) {
            String jwt = tokenResponse.getBody();
            String frontendUrl = "http://localhost:3000/oauth_success?token=" + URLEncoder.encode(jwt, "UTF-8");
            response.sendRedirect(frontendUrl);
        } else {
            // Handle the case when the user is not found in the database
        }


    }

}
