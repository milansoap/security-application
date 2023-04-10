package com.example.websecurity.config;

import com.example.websecurity.components.CustomOAuth2FailureHandler;
import com.example.websecurity.components.CustomOAuth2SuccessHandler;
import com.example.websecurity.dao.UserDao;
import io.jsonwebtoken.Jwt;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.security.auth.login.AccountLockedException;
import java.sql.SQLException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder;
    @Autowired
    @Lazy
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @Autowired
    @Lazy
    private CustomOAuth2FailureHandler customOAuth2FailureHandler;
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;


    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDao userDao,BCryptPasswordEncoder passwordEncoder) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .requestMatchers("/api/v1/auth/**","/error", "/oauth2/authorization/google","/login","/authenticate")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .oauth2Login()
                .loginProcessingUrl("/api/v1/auth/oauth2/success")
                .successHandler(customOAuth2SuccessHandler)
                .failureHandler(customOAuth2FailureHandler);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
       return new UserDetailsService() {
           @Override
           public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
               try {
                   return userDao.findUserByEmail(email, "basicLogin");
               } catch (SQLException | AccountLockedException e) {
                   throw new RuntimeException(e);
               }
           }
       };
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }




}
