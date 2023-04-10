package com.example.websecurity.components;

import com.example.websecurity.config.JWTUtill;
import com.example.websecurity.dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountLockedException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {


    private ApplicationContext applicationContext;
    private final JWTUtill jwtUtill;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserDetailsService userDetailsService;
    private final UserDao userDao;

    public CustomOAuth2SuccessHandler(JWTUtill jwtUtill, OAuth2AuthorizedClientService authorizedClientService, UserDetailsService userDetailsService, UserDao userDao) {
        this.jwtUtill = jwtUtill;
        this.authorizedClientService = authorizedClientService;
        this.userDetailsService = userDetailsService;
        this.userDao = userDao;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        System.out.print("method is called");
//        System.out.print(request);
//        System.out.print(response);
//        System.out.print(authentication);
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        UserDetails user;
//        System.out.println(userDao.findUserByEmail(email));
        System.out.println(email);
        try {
            System.out.println(userDao.findUserByEmail(email, "oauthLogin"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (AccountLockedException e) {
            throw new RuntimeException(e);
        }

//        if (user == null) {
//            System.out.println("USER IS NOT REGISTERED");
//        }

//        String jwt = jwtUtill.generateToken(user);

//        response.setContentType("application/json");
//        PrintWriter out = response.getWriter();
//        out.print("{\"token\":\"" + jwt + "\"}");
//        out.flush();
    }

}
